# twse-captcha-solver-java
[![GitHub license](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/coldnew/twse-captcha-solver/master/LICENSE)

A simple captcha solver for Taiwan Stock Exchange's [website](http://bsr.twse.com.tw/bshtm/). The algorithm is based on [hhschu](https://github.com/hhschu/Captcha_OCR)'s python method.

This project is written in Java, if you want C++ version, you can take a look at [coldnew/twse-captcha-solver](https://github.com/coldnew/twse-captcha-solver).

## Usage

You need to download the captcha image manually from [website](http://bsr.twse.com.tw/bshtm/), execute the tool in command line:

```sh
mvn compile exec:java -Dexec.mainClass="twse.brs.CaptchaSolver" -Dexec.args="xxx.png"
```

## Screenshot

![Screenshot](https://github.com/coldnew/twse-captcha-solver-java/raw/master/screenshot.png)

## Note

This application **ONLY** work on captcha like this:

![Sample](https://github.com/coldnew/twse-captcha-solver-java/raw/master/sample.png)

## License

Copyright © 2017 Yen-Chin, Lee <<coldnew.tw@gmail.com>>

Distributed under the [MIT License](http://opensource.org/licenses/MIT).
