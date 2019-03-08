#/*
# * Copyright (C) 2014  RoboPeak
# * Copyright (C) 2014 - 2018 Shanghai Slamtec Co., Ltd.
# *
# * This program is free software: you can redistribute it and/or modify
# * it under the terms of the GNU General Public License as published by
# * the Free Software Foundation, either version 3 of the License, or
# * (at your option) any later version.
# *
# * This program is distributed in the hope that it will be useful,
# * but WITHOUT ANY WARRANTY; without even the implied warranty of
# * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# * GNU General Public License for more details.
# *
# * You should have received a copy of the GNU General Public License
# * along with this program.  If not, see <http://www.gnu.org/licenses/>.
# *
# */
#

OPENCV_LIB_DIR=/usr/local/lib

HOME_TREE := /home/pi/rplidar_sdk/

WPILIB_ROOT_DIR=/usr/local/wpilib
WPILIB_INC_DIR=$(WPILIB_ROOT_DIR)/include

CSCORE_INC_DIR=$(WPILIB_INC_DIR)/cscore
WPILIB_LIB_DIR=$(WPILIB_ROOT_DIR)/lib
WPIUTIL_INC_DIR=$(WPILIB_INC_DIR)/wpiutil

MODULE_NAME := $(notdir $(CURDIR))

include $(HOME_TREE)/mak_def.inc

CXXSRC += lidarProc.cpp GripPipeline.cpp
C_INCLUDES += -I/home/pi/rplidar_sdk/sdk/include -I/home/pi/rplidar_sdk/sdk/src -I$(CSCORE_INC_DIR) -I$(WPIUTIL_INC_DIR)

EXTRA_OBJ := 
LD_LIBS += -lstdc++ -lpthread -lm -L$(OPENCV_LIB_DIR) -L$(WPILIB_LIB_DIR) -lopencv_core -lopencv_highgui -lopencv_imgproc -lopencv_features2d -lopencv_objdetect -lcscore -lwpiutil

all: build_app

include $(HOME_TREE)/mak_common.inc

clean: clean_app
