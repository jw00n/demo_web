REPOSITORY=/home/ubuntu/web/app/second/

PROJECT_NAME=demo

echo "> bulid 파일 복사"

sudo cp $REPOSITORY/zip/$PROJECT_NAME/build/libs/*.jar $REPOSITORY/

echo "> 현재 구동중인 애플리케이션 pid 확인"

CURRENT_PID=$(pgrep -f ${PROJECT_NAME} | grep jar | awk '{print $1}')

echo "> 실행중인 pid: $CURRENT_PID"

if [ -z "$CURRENT_PID" ]; then
        echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
        echo "> kill -15 $CURRENT_PID"
        sudo kill -15 $CURRENT_PID
        sudo sleep 5
fi
echo "> 새 어플리케이션 배포"
JAR_NAME=$(ls -tr REPOSITORY/*.jar | tail -n 1)

echo "> JAR_NAME: $JAR_NAME"

echo "> 권한 추가"
echo $JAR_NAME
sudo chmod 777 $REPOSITORY/$JAR_NAME


echo "> $JAR_NAME 실행"
sudo nohup java -jar \ 
    -Dspring.config.location=classpath:/application.properties, 
    /home/ubuntu/web/app/application-db.properties,
    /home/ubuntu/web/app/application-secret.properties \ 
    $REPOSITORY/$JAR_NAME 2>&1 &
