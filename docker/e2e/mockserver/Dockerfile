# A custom version of Mockserver with a shell and curl installed
# See:
# https://github.com/mock-server/mockserver/issues/1751
# https://github.com/mock-server/mockserver/issues/1895

ARG MOCKSERVER_VERSION

FROM busybox:1.38.0-uclibc@sha256:297dda192bda2157ddf40abb47a45a1090caff1864db9cfb9ce4b901ba318a3c AS busybox
FROM mockserver/mockserver:$MOCKSERVER_VERSION

COPY --from=busybox /bin /bin

USER root

ADD https://github.com/stunnel/static-curl/releases/download/8.2.1/curl-static-amd64-8.2.1.tar.xz curl.tar.xz
RUN tar -xf curl.tar.xz --directory /usr/bin

CMD [ "bin/sh" ]