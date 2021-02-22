# currencychecker
spring boot rest feign giphy openexchangerates mockito

to start the app you need to:
```
git clone https://github.com/himitsu1111/currencychecker.git
cd currencychecker
.\gradlew bootRun
```

to make docker image and run follow next steps: 
```
git clone https://github.com/himitsu1111/currencychecker.git
cd currencychecker
.\gradlew build
docker build -t currencychecker .
docker run -p 8080:8080 currencychecker
```
