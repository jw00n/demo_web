# 쉬고있는 profile 찾기

function find_idle_profile(){
    
    RESPONSE_CODE=$(curl -L -s -o /dev/null -w "%{http_code}" https://momodemo.ddns.net/profile)
    echo "$RESPONSE_CODE"
    echo "$CURRENT_PROFILE"
    if [ ${RESPONSE_CODE} -ge 400 ] # 400 보다 크면 (즉, 40x/50x 에러 모두 포함)
    then
        CURRENT_PROFILE=prof2
    else
        CURRENT_PROFILE=$(curl -L -s https://momodemo.ddns.net/profile)
    fi

    if [ ${CURRENT_PROFILE} == prof1 ]
    then
      IDLE_PROFILE=prof2
    else
      IDLE_PROFILE=prof1
    fi

    echo "${IDLE_PROFILE}"
}

# 쉬고 있는 profile의 port 찾기
function find_idle_port()
{
    IDLE_PROFILE=$(find_idle_profile)

    if [ ${IDLE_PROFILE} == prof1 ]
    then
      echo "8081"
    else
      echo "8082"
    fi
}
