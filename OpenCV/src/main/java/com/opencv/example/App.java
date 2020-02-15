package com.opencv.example;


import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class App{

    public static void main( String[] args ){
        nu.pattern.OpenCV.loadLibrary();

        Mat mat = Mat.eye(3, 3, CvType.CV_8UC1);
        System.out.println("mat = " + mat.dump());
    }
}
