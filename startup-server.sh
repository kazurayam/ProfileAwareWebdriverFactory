theDir=`echo $PWD`
cd ./src/web
groovy $theDir/httpserver.groovy -b .
cd $theDir
