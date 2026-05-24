# A custom version of Mockserver with a shell and curl installed
# See:
# https://github.com/mock-server/mockserver/issues/1751
# https://github.com/mock-server/mockserver/issues/1895

ARG MOCKSERVER_VERSION

FROM busybox:1.38.0-uclibc@sha256:eea4ff5612c911abd1d0e9ed47ba642547b01c3490877d9c1bb5fd6346462da4 AS busybox
FROM mockserver/mockserver:$MOCKSERVER_VERSION

COPY --from=busybox /bin /bin

USER root

ADD https://github.com/stunnel/static-curl/releases/download/8.2.1/curl-static-amd64-8.2.1.tar.xz curl.tar.xz
RUN tar -xf curl.tar.xz --directory /usr/bin

CMD [ "bin/sh" ]