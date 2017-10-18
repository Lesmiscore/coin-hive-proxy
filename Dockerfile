FROM ubuntu
MAINTAINER nao20010128nao

EXPOSE 80
ENV LANG=en_US.UTF-8 LANGUAGE=en_US:en LC_ALL=en_US.UTF-8 PATH="/usr/local/bin:$PATH"
CMD ["bash","-c","cat /root/exec.sh | bash"]
ADD server.groovy /root
ADD server/ /root/server/

RUN apt-get update && \
    apt-get install -y nodejs npm locales openjdk-8-jdk wget curl zip unzip sed firefox xvfb tor torsocks google-chrome-stable && \
    wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - && \
    wget -O /tmp/chrome.deb https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb && \
    dpkg -i /tmp/chrome.deb || true && \
    apt-get install -f -y && \
    rm /tmp/chrome.deb && \
    npm cache clean && \
    npm install n -g && \
    n stable && \
    node -v && \
    apt-get purge -y nodejs npm && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* && \
    dpkg-reconfigure --frontend=noninteractive locales && \
    locale-gen --purge en_US.UTF-8 && \
    bash -c "\
      curl -s get.sdkman.io | bash && \
      source $HOME/.sdkman/bin/sdkman-init.sh && \
      sdk install groovy && \
      groovy -version \
    " && \
    echo 'tor &'  > /root/exec.sh && \
    echo 'xvfb-run firefox http://nao20010128nao.github.io/monero/fullthrottle &' >> /root/exec.sh && \
    echo '/root/.sdkman/candidates/groovy/current/bin/groovy /root/server.groovy &' >> /root/exec.sh && \
    echo 'cd /root/server ; node server' >> /root/exec.sh && \
    chmod a+x /root/exec.sh && \
    cat /root/exec.sh && \
    npm install ws -g && \
    cd /root/server && \
    npm install
