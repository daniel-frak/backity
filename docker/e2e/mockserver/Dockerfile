# A custom version of Mockserver with a shell and curl installed
# See:
# https://github.com/mock-server/mockserver/issues/1751
# https://github.com/mock-server/mockserver/issues/1895

ARG MOCKSERVER_VERSION

FROM busybox:1.38.0-uclibc@sha256:b699b0c739b2d039afd8e2488a3ccfc0b9a86301d87633448d4a93c57bee8c76 AS busybox
FROM mockserver/mockserver:$MOCKSERVER_VERSION

COPY --from=busybox /bin /bin

USER root

ADD https://github.com/stunnel/static-curl/releases/download/8.2.1/curl-static-amd64-8.2.1.tar.xz curl.tar.xz
RUN tar -xf curl.tar.xz --directory /usr/bin

CMD [ "bin/sh" ]