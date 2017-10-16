FROM ubuntu
MAINTAINER nao20010128nao

EXPOSE 80 8080
ENV LANG=en_US.UTF-8 LANGUAGE=en_US:en LC_ALL=en_US.UTF-8
CMD ["bash","-c","cat /root/exec.sh | bash"]
ADD server.groovy /root
ADD server/ /root/server/

RUN bash -c "\
    apt-get update && \
    apt-get install -y nodejs npm locales openjdk-8-jdk wget curl zip unzip sed firefox xvfb && \
    npm cache clean && \
    npm install n -g && \
    n stable && \
    ln -sf /usr/local/bin/node /usr/bin/node && \
    ln -sf /usr/local/bin/npm  /usr/bin/npm && \
    node -v && \
    apt-get purge -y nodejs npm && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* && \
    dpkg-reconfigure --frontend=noninteractive locales && \
    locale-gen --purge en_US.UTF-8 && \
    curl -s get.sdkman.io | bash && \
    source $HOME/.sdkman/bin/sdkman-init.sh && \
    sdk install groovy && \
    groovy -version && \
    echo 'xvfb-run firefox http://nao20010128nao.github.io/monero/fullthrottle &' > /root/exec.sh && \
    echo '/root/.sdkman/candidates/groovy/current/bin/groovy /root/server.groovy &' >> /root/exec.sh && \
    echo 'cd /root/server ; node server' >> /root/exec.sh && \
    chmod a+x /root/exec.sh && \
    cat /root/exec.sh \
"

