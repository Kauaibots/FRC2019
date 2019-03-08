#include <stdio.h>
#include <stdlib.h>
#include <cmath>
#include <opencv2/opencv.hpp>
#include <cscore.h>

#include "rplidar.h" //RPLIDAR standard sdk, all-in-one header
#include "GripPipeline.h"


#ifndef _countof
#define _countof(_Array) (int)(sizeof(_Array) / sizeof(_Array[0]))
#endif

#ifdef _WIN32
#include <Windows.h>
#define delay(x)   ::Sleep(x)
#else
#include <unistd.h>
static inline void delay(_word_size_t ms){
    while (ms>=1000){
        usleep(1000*1000);
        ms-=1000;
    };
    if (ms!=0)
        usleep(ms*1000);
}
#endif

using namespace rp::standalone::rplidar;
using namespace cv;
using namespace grip;
using namespace cs;
using namespace std;


float maxAngle = 80.0f;
float minAngle = 280.0f;

int frameWidth = 1000;
int frameHeight = 1000;

double displayedLidarRange = 2.5; //In meters; mulitpied by 2 = milimeters per pixel

bool checkRPLIDARHealth(RPlidarDriver * drv)
{
    u_result     op_result;
    rplidar_response_device_health_t healthinfo;


    op_result = drv->getHealth(healthinfo);
    if (IS_OK(op_result)) { // the macro IS_OK is the preperred way to judge whether the operation is succeed.
        printf("RPLidar health status : %d\n", healthinfo.status);
        if (healthinfo.status == RPLIDAR_STATUS_ERROR) {
            fprintf(stderr, "Error, rplidar internal error detected. Please reboot the device to retry.\n");
            // enable the following code if you want rplidar to be reboot by software
            // drv->reset();
            return false;
        } else {
            return true;
        }

    } else {
        fprintf(stderr, "Error, cannot retrieve the lidar health code: %x\n", op_result);
        return false;
    }
}


void getCartesian(float theta, float distance, Point& point){
    
    //Makes 0 degrees straight forward and inverts rotation (object moving clockwise around lidar moves clockwise in data).
    theta -= 90;
    if (theta < 0.0){
        theta += 360.0;
    }
    theta = 360-theta;

    theta = theta*(M_PI/180.0);

    float x = distance*cos(theta);
    float y = distance*sin(theta);


    point.x = x;
    point.y = y;
}

//Adapt a point to be displayed onto a window with (0,0) in center of frame
void prepDisplay(Point& point){
    float x = point.x;
    float y  = point.y;

    //One half of the divisor is equal to range in meters from lidar displayed.
    x /= displayedLidarRange*2;
    y /= displayedLidarRange*2;

    x += frameWidth/2;
    y -= frameHeight/2;
    y = -y;

    point.x = x;
    point.y = y;
}

void removeDisplay(Point& point){
    float x = point.x;
    float y  = point.y;

    //One half of the divisor is equal to range in meters from lidar displayed.
    x *= displayedLidarRange*2;
    y *= displayedLidarRange*2;

    x -= frameWidth/2;
    y += frameHeight/2;
    y = -y;

    point.x = x;
    point.y = y;
}

void drawLines(Mat matOutput, vector<grip::Line> lines, Scalar color){
    for( size_t i = 0; i < lines.size(); i++ )
    {
        Point pt1;
        Point pt2;
        pt1.x = lines[i].x1;
        pt1.y = lines[i].y1;
        pt2.x = lines[i].x2;
        pt2.y = lines[i].y2;
        line(matOutput, pt1, pt2, color, 2, CV_AA);

        printf("Drawing Lines  %d  %d", pt1.x, pt2.x);
    }
}



#include <signal.h>
bool ctrl_c_pressed;
void ctrlc(int)
{
    ctrl_c_pressed = true;
}



int main(int argc, const char * argv[]) {
    const char * opt_com_path = NULL;
    _u32         baudrateArray[2] = {115200, 256000};
    _u32         opt_com_baudrate = 0;
    u_result     op_result;

    bool useArgcBaudrate = false;

    printf("Ultra simple LIDAR data grabber for RPLIDAR.\n"
           "Version: %s \n", RPLIDAR_SDK_VERSION);

    // read serial port
    opt_com_path = "/dev/rplidar";

    // read baud rate from the command line if specified...
    if (argc>2)
    {
        opt_com_baudrate = strtoul(argv[2], NULL, 10);
        useArgcBaudrate = true;
    }

    // create the driver instance
	RPlidarDriver * drv = RPlidarDriver::CreateDriver(DRIVER_TYPE_SERIALPORT);
    if (!drv) {
        fprintf(stderr, "insufficent memory, exit\n");
        exit(-2);
    }
    
    rplidar_response_device_info_t devinfo;
    bool connectSuccess = false;
    // make connection...
    if(useArgcBaudrate)
    {
        if(!drv)
            drv = RPlidarDriver::CreateDriver(DRIVER_TYPE_SERIALPORT);
        if (IS_OK(drv->connect(opt_com_path, opt_com_baudrate)))
        {
            op_result = drv->getDeviceInfo(devinfo);

            if (IS_OK(op_result)) 
            {
                connectSuccess = true;
            }
            else
            {
                delete drv;
                drv = NULL;
            }
        }
    }
    else
    {
        size_t baudRateArraySize = (sizeof(baudrateArray))/ (sizeof(baudrateArray[0]));
        for(size_t i = 0; i < baudRateArraySize; ++i)
        {
            if(!drv)
                drv = RPlidarDriver::CreateDriver(DRIVER_TYPE_SERIALPORT);
            if(IS_OK(drv->connect(opt_com_path, baudrateArray[i])))
            {
                op_result = drv->getDeviceInfo(devinfo);

                if (IS_OK(op_result)) 
                {
                    connectSuccess = true;
                    break;
                }
                else
                {
                    delete drv;
                    drv = NULL;
                }
            }
        }
    } 

    GripPipeline pipeline;

    pipeline.setMMPerPix(displayedLidarRange);
    CvSource cvsource("cvsource",
		VideoMode::PixelFormat::kMJPEG, 1000, 1000, 10);
	MjpegServer processedVideoServer("processed_video_server", 8080);
	processedVideoServer.SetSource(cvsource);

    if (!connectSuccess) {
        
        fprintf(stderr, "Error, cannot bind to the specified serial port %s.\n"
            , opt_com_path);
        goto on_finished;
    }

    // print out the device serial number, firmware and hardware version number..
    printf("RPLIDAR S/N: ");
    for (int pos = 0; pos < 16 ;++pos) {
        printf("%02X", devinfo.serialnum[pos]);
    }

    printf("\n"
            "Firmware Ver: %d.%02d\n"
            "Hardware Rev: %d\n"
            , devinfo.firmware_version>>8
            , devinfo.firmware_version & 0xFF
            , (int)devinfo.hardware_version);



    // check health...
    if (!checkRPLIDARHealth(drv)) {
        goto on_finished;
    }

    signal(SIGINT, ctrlc);
    
    drv->startMotor();
    drv->setMotorPWM(400);
    // start scan...
    drv->startScan(0,1);

    namedWindow( "Display window", WINDOW_AUTOSIZE );
    namedWindow ("Dilate/Erode", WINDOW_AUTOSIZE);


    // fetech result and print it out...
    while (1) {
        rplidar_response_measurement_node_t nodes[8192];
        size_t   count = _countof(nodes);

        Point pointArr[8192];

        op_result = drv->grabScanData(nodes, count);

        Mat mat(frameWidth, frameHeight, CV_8UC3, Scalar(255,255,255));

        if (IS_OK(op_result)) {
            drv->ascendScanData(nodes, count);
            for (int pos = 0; pos < (int)count ; ++pos) {
                float theta = (nodes[pos].angle_q6_checkbit >> RPLIDAR_RESP_MEASUREMENT_ANGLE_SHIFT)/64.0f;
                float distance = nodes[pos].distance_q2/4.0f;
                int quality = nodes[pos].sync_quality >> RPLIDAR_RESP_MEASUREMENT_QUALITY_SHIFT;
                printf("%s theta: %03.2f Dist: %08.2f Q: %d \n", 
                    (nodes[pos].sync_quality & RPLIDAR_RESP_MEASUREMENT_SYNCBIT) ?"S ":"  ", 
                    theta,
                    distance,
                    quality);

                getCartesian(theta, distance, pointArr[pos]);
                prepDisplay(pointArr[pos]);
                circle(mat, pointArr[pos], 2, Scalar(0,0,0), -1);
            }


            pipeline.Process(mat);
            imshow( "Dilate/Erode", *(pipeline.GetCvErode1Output()));
            //drawContours( mat, *(pipeline.GetFilterContoursOutput()), -1, Scalar(0,0,255), 2); 
            drawLines(mat, *(pipeline.GetFilterLinesOutput()), Scalar(0,0,255));           
            cvsource.PutFrame(mat);
            imshow( "Display window", mat);
            waitKey(50);
        }

        if (ctrl_c_pressed){ 
            break;
        }
    }

    drv->stop();
    drv->stopMotor();
    // done!
on_finished:
    RPlidarDriver::DisposeDriver(drv);
    drv = NULL;
    return 0;
}

