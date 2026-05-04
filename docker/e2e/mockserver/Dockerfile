# A custom version of Mockserver with a shell and curl installed
# See:
# https://github.com/mock-server/mockserver/issues/1751
# https://github.com/mock-server/mockserver/issues/1895

ARG MOCKSERVER_VERSION

FROM busybox:1.37.0-uclibc@sha256:e3a499df044f0129fd2e82a63177e7affe66ba6fd73acc9e231fd7695f06e5e2 AS busybox
FROM mockserver/mockserver:$MOCKSERVER_VERSION

COPY --from=busybox /bin /bin

USER root

ADD https://github.com/stunnel/static-curl/releases/download/8.2.1/curl-static-amd64-8.2.1.tar.xz curl.tar.xz
RUN tar -xf curl.tar.xz --directory /usr/bin

CMD [ "bin/sh" ]