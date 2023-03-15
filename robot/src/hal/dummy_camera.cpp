//
// Created by Lea Wü on 28.03.21.
//
#include "hal/dummy_camera.h"
#ifndef UNIT_TEST
#include <SD_MMC.h>
#include <FS.h>
#endif
#include <iostream>

//the dummy_camera is for testing without a real camera

//Constructor
dummy_camera::dummy_camera() {
    image.buf = nullptr;
    image.len = 0;
    imageNumber = 1;
}

//Destructor
dummy_camera::~dummy_camera() {

}

//because the esp32 has not enough storage the image is on an sd card (the esp32 has an micro sd card slot for a card with max. 4GB)
i_camera::image_t dummy_camera::get_image() {
#ifndef UNIT_TEST
    sprintf(fileNumber, "%05d", imageNumber++);
    std::string imageFilename = "/image"+std::string(fileNumber)+".jpg";
    fs::FS &fs = SD_MMC;
    File imgFile = fs.open(imageFilename.c_str(), FILE_READ);
    if (!imgFile) {
        std::cout << "Failed to open file in reading mode" << std::endl;
    } else {
        imgFile.read(image.buf, imgFile.size());
        image.len = imgFile.size();
        std::cout << "Read " + imageFilename << std::endl;
        imageNumber++;
    }
    imgFile.close();
#else

#endif

    return image;
}