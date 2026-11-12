# A custom version of Mockserver with a shell and curl installed
# See:
# https://github.com/mock-server/mockserver/issues/1751
# https://github.com/mock-server/mockserver/issues/1895

FROM busybox:1.38.0-uclibc@sha256:b2e35951f72501f6a99f1bf45bcfe6d4795be08c518c2f97d0ed2fe776958a3b AS busybox
FROM mockserver/mockserver:5.15.0

COPY --from=busybox /bin /bin

USER root

ADD https://github.com/stunnel/static-curl/releases/download/8.2.1/curl-static-amd64-8.2.1.tar.xz curl.tar.xz
RUN tar -xf curl.tar.xz --directory /usr/bin

CMD [ "bin/sh" ]