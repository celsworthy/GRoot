#!/bin/bash
#sudo /home/pi/Groot/jdk-11.0.2/bin/java -Dmonocle.input.0/0/0/0.minX=200 -Dmonocle.input.0/0/0/0.maxX=3900 -Dmonocle.input.0/0/0/0.minY=3900 -Dmonocle.input.0/0/0/0.maxY=200 -Dmonocle.input.0/0/0/0.flipXY=true -jar ./GRoot.jar
sudo /home/pi/Groot/jdk-11.0.2/bin/java -Dmonocle.input.0/0/0/0.minX=3840 -Dmonocle.input.0/0/0/0.maxX=1600 -Dmonocle.input.0/0/0/0.minY=400 -Dmonocle.input.0/0/0/0.maxY=6400  -Dmonocle.input.0/0/0/0.flipXY=true -jar ./GRoot.jar -u 500
#sudo /home/pi/Groot/jdk-11.0.2/bin/java -Dmonocle.input.0/0/0/0.minX=3900 -Dmonocle.input.0/0/0/0.maxX=200 -Dmonocle.input.0/0/0/0.minY=200 -Dmonocle.input.0/0/0/0.maxY=3900 -Dmonocle.input.0/0/0/0.flipXY=true com.sun.glass.ui.monocle.GetEvent
#sudo /home/pi/Groot/jdk-11.0.2/bin/java -Dmonocle.screen.fb=/dev/fb1 -Dprism.order=sw -jar ./GRoot.jar
