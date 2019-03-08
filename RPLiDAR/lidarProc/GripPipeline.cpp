#include "GripPipeline.h"

namespace grip {

GripPipeline::GripPipeline() {
}

	double MMPerPix = 5;

	int cvErode0Iterations = 1.0;  // default Double
	int cvDilateIterations = 8.0;  // default Double
	int cvErode1Iterations = 4.0;  // default Double



/**
* Runs an iteration of the pipeline and updates outputs.
*/
void GripPipeline::Process(cv::Mat& source0){
	cv::namedWindow("Sliders", cv::WINDOW_AUTOSIZE);

	cv::createTrackbar ("Erode 0", "Sliders", &cvErode0Iterations, 20, 0);
	cv::createTrackbar ("Dilate", "Sliders", &cvDilateIterations, 20, 0);
	cv::createTrackbar ("Erode 1", "Sliders", &cvErode1Iterations, 20, 0);
	
	//Step HSV_Threshold0:
	//input
	cv::Mat hsvThresholdInput = source0;
	double hsvThresholdHue[] = {0.0, 27.952218430034147};
	double hsvThresholdSaturation[] = {0.0, 26.544368600682592};
	double hsvThresholdValue[] = {0.0, 35.247440273037554};
	hsvThreshold(hsvThresholdInput, hsvThresholdHue, hsvThresholdSaturation, hsvThresholdValue, this->hsvThresholdOutput);
	//Step CV_erode0:
	//input
	cv::Mat cvErode0Src = hsvThresholdOutput;
	cv::Mat cvErode0Kernel;
	cv::Point cvErode0Anchor(-1, -1);
    int cvErode0Bordertype = cv::BORDER_CONSTANT;
	cv::Scalar cvErode0Bordervalue(-1);
	cvErode(cvErode0Src, cvErode0Kernel, cvErode0Anchor, cvErode0Iterations, cvErode0Bordertype, cvErode0Bordervalue, this->cvErode0Output);
	//Step CV_dilate0:
	//input
	cv::Mat cvDilateSrc = cvErode0Output;
	cv::Mat cvDilateKernel;
	cv::Point cvDilateAnchor(-1, -1);
    int cvDilateBordertype = cv::BORDER_CONSTANT;
	cv::Scalar cvDilateBordervalue(-1);
	cvDilate(cvDilateSrc, cvDilateKernel, cvDilateAnchor, cvDilateIterations, cvDilateBordertype, cvDilateBordervalue, this->cvDilateOutput);
	//Step CV_erode1:
	//input
	cv::Mat cvErode1Src = cvDilateOutput;
	cv::Mat cvErode1Kernel;
	cv::Point cvErode1Anchor(-1, -1);
    int cvErode1Bordertype = cv::BORDER_CONSTANT;
	cv::Scalar cvErode1Bordervalue(-1);
	cvErode(cvErode1Src, cvErode1Kernel, cvErode1Anchor, cvErode1Iterations, cvErode1Bordertype, cvErode1Bordervalue, this->cvErode1Output);
	//Step Find_Lines0:
	//input
	cv::Mat findLinesInput = cvErode1Output;
	findLines(findLinesInput, this->findLinesOutput);
	//Step Filter_Lines0:
	//input
	std::vector<Line> filterLinesLines = findLinesOutput;
	double filterLinesMinLength = 150/MMPerPix;  // default Double
	double filterLinesAngle[] = {0, 360};
	filterLines(filterLinesLines, filterLinesMinLength, filterLinesAngle, this->filterLinesOutput);
}

/**
 * This method is a generated getter for the output of a HSV_Threshold.
 * @return Mat output from HSV_Threshold.
 */
cv::Mat* GripPipeline::GetHsvThresholdOutput(){
	return &(this->hsvThresholdOutput);
}
/**
 * This method is a generated getter for the output of a CV_erode.
 * @return Mat output from CV_erode.
 */
cv::Mat* GripPipeline::GetCvErode0Output(){
	return &(this->cvErode0Output);
}
/**
 * This method is a generated getter for the output of a CV_dilate.
 * @return Mat output from CV_dilate.
 */
cv::Mat* GripPipeline::GetCvDilateOutput(){
	return &(this->cvDilateOutput);
}
/**
 * This method is a generated getter for the output of a CV_erode.
 * @return Mat output from CV_erode.
 */
cv::Mat* GripPipeline::GetCvErode1Output(){
	return &(this->cvErode1Output);
}
/**
 * This method is a generated getter for the output of a Find_Lines.
 * @return LinesReport output from Find_Lines.
 */
std::vector<Line>* GripPipeline::GetFindLinesOutput(){
	return &(this->findLinesOutput);
}
/**
 * This method is a generated getter for the output of a Filter_Lines.
 * @return LinesReport output from Filter_Lines.
 */
std::vector<Line>* GripPipeline::GetFilterLinesOutput(){
	return &(this->filterLinesOutput);
}
	/**
	 * Segment an image based on hue, saturation, and value ranges.
	 *
	 * @param input The image on which to perform the HSL threshold.
	 * @param hue The min and max hue.
	 * @param sat The min and max saturation.
	 * @param val The min and max value.
	 * @param output The image in which to store the output.
	 */
	void GripPipeline::hsvThreshold(cv::Mat &input, double hue[], double sat[], double val[], cv::Mat &out) {
		cv::cvtColor(input, out, cv::COLOR_BGR2HSV);
		cv::inRange(out,cv::Scalar(hue[0], sat[0], val[0]), cv::Scalar(hue[1], sat[1], val[1]), out);
	}

	/**
	 * Expands area of higher value in an image.
	 * @param src the Image to dilate.
	 * @param kernel the kernel for dilation.
	 * @param anchor the center of the kernel.
	 * @param iterations the number of times to perform the dilation.
	 * @param borderType pixel extrapolation method.
	 * @param borderValue value to be used for a constant border.
	 * @param dst Output Image.
	 */
	void GripPipeline::cvDilate(cv::Mat &src, cv::Mat &kernel, cv::Point &anchor, double iterations, int borderType, cv::Scalar &borderValue, cv::Mat &dst) {
		cv::dilate(src, dst, kernel, anchor, (int)iterations, borderType, borderValue);
	}

	/**
	 * Expands area of lower value in an image.
	 * @param src the Image to erode.
	 * @param kernel the kernel for erosion.
	 * @param anchor the center of the kernel.
	 * @param iterations the number of times to perform the erosion.
	 * @param borderType pixel extrapolation method.
	 * @param borderValue value to be used for a constant border.
	 * @param dst Output Image.
	 */
	void GripPipeline::cvErode(cv::Mat &src, cv::Mat &kernel, cv::Point &anchor, double iterations, int borderType, cv::Scalar &borderValue, cv::Mat &dst) {
		cv::erode(src, dst, kernel, anchor, (int)iterations, borderType, borderValue);
	}

	/**
	 * Finds all line segments in an image.
	 *
	 * @param input The image on which to perform the find lines.
	 * @param lineList The output where the lines are stored.
	 */
	void GripPipeline::findLines(cv::Mat &input, std::vector<Line> &lineList) {
		cv::Ptr<cv::LineSegmentDetector> lsd = cv::createLineSegmentDetector(cv::LSD_REFINE_STD);
		std::vector<cv::Vec4i> lines;
		lineList.clear();
		if (input.channels() == 1) {
			lsd->detect(input, lines);
		} else {
			// The line detector works on a single channel.
			cv::Mat tmp;
			cv::cvtColor(input, tmp, cv::COLOR_BGR2GRAY);
			lsd->detect(tmp, lines);
		}
		// Store the lines in the LinesReport object
		if (!lines.empty()) {
			for (int i = 0; i < lines.size(); i++) {
				cv::Vec4i line = lines[i];
				lineList.push_back(Line(line[0], line[1], line[2], line[3]));
			}
		}
	}

	/**
	 * Filters out lines that do not meet certain criteria.
	 *
	 * @param inputs The lines that will be filtered.
	 * @param minLength The minimum length of a line to be kept.
	 * @param angle The minimum and maximum angle of a line to be kept.
	 * @param outputs The output lines after the filter.
	 */
	void GripPipeline::filterLines(std::vector<Line> &inputs, double minLength, double angle[], std::vector<Line> &outputs) {
	outputs.clear();
	for (Line line: inputs) {
		if (line.length()>abs(minLength)) {
			if ((line.angle() >= angle[0] && line.angle() <= angle[1]) ||
					(line.angle() + 180.0 >= angle[0] && line.angle() + 180.0 <=angle[1])) {
				outputs.push_back(line);
			}
		}
	}
	}

	void GripPipeline::setMMPerPix(double lidarRange){
		MMPerPix = lidarRange*2;
	}



} // end grip namespace

