## How to build .deb package
In order to build the debian package you'll need:
* git
* debuild


`sudo apt-get install git debuild`


1. git clone https://github.com/libersoft/libfirmapiu-java.git
2. cd libfirmapiu-java
3. git checkout debian
4. cd debian
5. debuild -uc -us

Now you should have your .deb package located in the parent folder (..)

