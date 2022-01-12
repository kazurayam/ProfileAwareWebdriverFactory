theDir=`echo $PWD`
cd ./src/web
groovy $theDir/httpserver.groovy -b . --debug
cd $theDir
