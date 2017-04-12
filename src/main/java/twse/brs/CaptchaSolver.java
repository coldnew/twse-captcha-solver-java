/*
 * CaptchaSolver.java
 *
 * Copyright (c) 2017 Yen-Chin, Lee <coldnew.tw@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package twse.brs;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.tesseract.TessBaseAPI;

import static org.bytedeco.javacpp.opencv_imgcodecs.IMREAD_GRAYSCALE;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_OTSU;
import static org.bytedeco.javacpp.opencv_imgproc.MORPH_RECT;
import static org.bytedeco.javacpp.opencv_imgproc.THRESH_BINARY;
import static org.bytedeco.javacpp.opencv_imgproc.erode;
import static org.bytedeco.javacpp.opencv_imgproc.getStructuringElement;
import static org.bytedeco.javacpp.opencv_imgproc.threshold;
import static org.bytedeco.javacpp.opencv_photo.fastNlMeansDenoising;

/**
 * Describe class <code>CaptchaSolver</code> here.
 *
 * @author <a href="mailto:coldnew.tw@gmail.com">Yen-Chin, Lee</a>
 * @version 1.0
 */
public class CaptchaSolver
{
    private TessBaseAPI tess;

    /**
     * Creates a new <code>CaptchaSolver</code> instance.
     *
     */
    public CaptchaSolver() {
        // use builtin resources
        this(CaptchaSolver.class.getResource("/tessdata").getPath());
    }

    /**
     * Creates a new <code>CaptchaSolver</code> instance.
     *
     * @param tessdataPath a <code>String</code> value
     */
    public CaptchaSolver(String tessdataPath) {
        tess = new TessBaseAPI();

        // Initialize tesseract-ocr with English, without specifying tessdata path
        if (tess.Init(tessdataPath, "eng") != 0) {
            throw new IllegalStateException("Could not initialize tesseract.");
        }
    }

    /**
     * Describe <code>finalize</code> method here.
     *
     */
    protected void finalize() {
        tess.End();
    }

    private Mat clean_captcha(String file) {

        // Load captcha captcha in grayscale
        Mat captcha = imread(file, IMREAD_GRAYSCALE);
        if (captcha.empty()) {
            System.out.println("Can't read captcha image '" + file + "'");
            return captcha;
        }

        // Convert the captcha to black and white.
        Mat captcha_bw = new Mat();
        threshold(captcha, captcha_bw, 128, 255, THRESH_BINARY | CV_THRESH_OTSU);

        // Erode the image to remove dot noise and that wierd line. I use a 3x3
        // rectengal as the kernal.
        Mat captcha_erode = new Mat();
        Mat element = getStructuringElement(MORPH_RECT, new Size(3, 3));
        erode(captcha_bw, captcha_erode, element);

        // Some cosmetic
        Mat captcha_denoise = new Mat();
        fastNlMeansDenoising(captcha_erode, captcha_denoise, 7, 7, 21);

        return captcha_denoise;
    }

    private String image_to_string(Mat img) {
        BytePointer outText;

        tess.SetImage(img.data(), img.cols(), img.rows(), 1, img.cols());

        outText = tess.GetUTF8Text();
        String s = outText.getString();

        // Destroy used object and release memory
        outText.deallocate();

        return s.replaceAll("[^0-9-A-Z]", "");
    }

    /**
     * Describe <code>solve</code> method here.
     *
     * @param file a <code>String</code> value
     * @return a <code>String</code> value
     */
    public String solve(String file) {
        return image_to_string(clean_captcha(file));
    }

    /**
     * Describe <code>main</code> method here.
     *
     * @param args a <code>String</code> value
     */
    public static void main(String[] args)
    {
        CaptchaSolver cs = new CaptchaSolver();
        System.out.println("Captcha: " + cs.solve(args[0]));
    }
}