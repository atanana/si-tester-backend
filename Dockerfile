FROM openjdk:11

ENV APPLICATION_USER si-tester
RUN useradd -ms /bin/bash $APPLICATION_USER

RUN mkdir /app
RUN chown -R $APPLICATION_USER /app

USER $APPLICATION_USER

COPY ./build/install/si-tester/ /app/
WORKDIR /app

EXPOSE 8000

CMD ["./bin/si-tester"]