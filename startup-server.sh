theDir=`echo $PWD`
cd ~/github/s3_of_kazurayam.com/src/web
groovy $theDir/httpserver.groovy -b . -p 8090
cd $theDir