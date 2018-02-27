#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <time.h>

#include "json.h"

#define HELO_PORT 42742
#define HELO_GROUP "239.255.255.250"

/*

 Reading:

    https://www.gitbook.com/book/hi3518/hisilicon-sdk-install/details

  Prerequisite:

    Install Virtual Box i386 with 32 gigabyte disk

    Install Debian into Virtual Box

    Do not install any GCC into Debian!

 Download SDK:

    https://app.box.com/s/cibs7n1mgvhqaqjlidtveegu1uajt5yr/folder/18989615567

 Install SDK:

     Get and unpack Hi3518E_V200R001C01SPC040.zip (ignore errors)

      ▾ Hi3518E V200R001C01SPC040/
        ▸ 00.hardware/
        ▾ 01.software/
          ▾ board/
            ▸ document_cn/
            ▸ document_en/
            ▸ Hi3518E_SDK_V1.0.4.0.tgz

     Inside unpack Hi3518E_SDK_V1.0.4.0.tgz

      ▾ Hi3518E V200R001C01SPC040/
        ▸ 00.hardware/
        ▾ 01.software/
          ▾ board/
            ▸ document_cn/
            ▸ document_en/
            ▾ Hi3518E_SDK_V1.0.4.0/
              ▸ drv/
              ▸ mpp/
              ▸ osdrv/
              ▸ package/
              ▸ scripts/
                sdk.cleanup*
                sdk.unpack*

     Inside call "bash sdk.unpack"

             ▾ Hi3518E_SDK_V1.0.4.0/
              ▸ drv/
              ▸ mpp/
              ▾ osdrv/
                ▾ opensource/
                  ▸ busybox/
                  ▸ kernel/
                  ▾ toolchain/
                    ▸ arm-hisiv300-linux/
                    ▾ arm-hisiv400-linux/
                      ▸ runtime_lib/
                        arm-hisiv400-linux.tar.bz2
                        cross.v400.install*

            chmod 777 cross.v400.install
            sudo ./cross.v400.install

            sudo dpkg --add-architecture i386

     Check /opt/hisi-linux/x86-arm/arm-hisiv400-linux/

     Install uClibc Stuff

            ▾ Hi3518E_SDK_V1.0.4.0/
              ▸ drv/
              ▸ mpp/
              ▸ osdrv/
              ▾ package/
                drv.tgz
                mpp.tgz
                osdrv.tgz
                rootfs_uclibc.tgz

     Inside unpack rootfs_uclibc.tgz

            sudo mv rootfs_uclibc /opt/hisi-linux/x86-arm/arm-hisiv400-linux/

 Compile:

     /opt/hisi-linux/x86-arm/arm-hisiv400-linux/target/bin/arm-hisiv400-linux-gcc \
        -Wall -c ~/TVPush/app/src/main/cpp/meme.c -o meme.o

 Link with uClibc:

     /opt/hisi-linux/x86-arm/arm-hisiv400-linux/bin/arm-hisiv400-linux-gnueabi-ld \
        --sysroot=/opt/hisi-linux/x86-arm/arm-hisiv400-linux/target \
        --eh-frame-hdr -X -m armelf_linux_eabi \
        --dynamic-linker=/lib/ld-uClibc.so.0 \
        --entry main \
        /opt/hisi-linux/x86-arm/arm-hisiv400-linux/rootfs_uclibc/lib/libuClibc-0.9.33.2.so \
        /opt/hisi-linux/x86-arm/arm-hisiv400-linux/rootfs_uclibc/lib/ld-uClibc.so.0 \
        -o meme meme.o

 Done!

*/

#define MSGBUFSIZE 4096

char messbuff[ MSGBUFSIZE ];
char memebuff[ MSGBUFSIZE ];

char nam[ 256 ];
char nck[ 256 ];
char loc[ 256 ];
char mod[ 256 ];
char ver[ 256 ];
char uid[ 256 ];

char did[ 32 ];
char cid[ 32 ];
char cpw[ 32 ];
char dpw[ 32 ];

int exitloop;

void strtrim(char *str)
{
    while ((strlen(str) > 0) && ((str[ strlen(str) - 1 ] == '\n') || (str[ strlen(str) - 1 ] == '\r')))
    {
        str[ strlen(str) - 1 ] = 0;
    }
}

void getDeviceInfo()
{
    FILE *fd = fopen("/etc/back.bin", "rb");

    if (fd == NULL) fd = fopen("./back.bin", "rb");

    if (fd != NULL)
    {
        char backbin[256];

        size_t xfer = fread(backbin, 1, sizeof(backbin), fd);

        if (xfer > 0)
        {
            strncpy(did, backbin + 4, sizeof(did));
            strncpy(cid, backbin + 4 + 32, sizeof(cid));
            strncpy(cpw, backbin + 4 + 64, sizeof(cpw));

            printf("did=%s\n", did);
            printf("cid=%s\n", cid);
            printf("cpw=%s\n", cpw);
        }

        fclose(fd);
    }
}

void getHackInfo()
{
    FILE *fd = fopen("/home/yi-hack-v3/.hackinfo", "rb");

    if (fd == NULL) fd = fopen("./hackinfo", "rb");

    if (fd != NULL)
    {
        char line[1024];

        while (fgets(line, sizeof(line), fd))
        {
            strtrim(line);

            if (strncmp(line, "CAMERA=", 7) == 0)
            {
                strncpy(mod, line + 7, sizeof(mod));

                printf("mod=%s\n", mod);
            }

            if (strncmp(line, "VERSION=", 8) == 0)
            {
                strncpy(ver, line + 8, sizeof(ver));

                printf("ver=%s\n", ver);
            }
        }

        fclose(fd);
    }
}

void getCustomInfo()
{
    FILE *fd = fopen("/etc/meme.txt", "rb");

    if (fd == NULL) fd = fopen("./meme.txt", "rb");

    if (fd != NULL)
    {
        char line[1024];

        while (fgets(line, sizeof(line), fd))
        {
            strtrim(line);

            if (strncmp(line, "name=", 5) == 0)
            {
                strncpy(nam, line + 5, sizeof(nam));

                printf("nam=%s\n", nam);
            }

            if (strncmp(line, "nick=", 5) == 0)
            {
                strncpy(nck, line + 5, sizeof(nck));

                printf("nck=%s\n", nck);
            }

            if (strncmp(line, "location=", 9) == 0)
            {
                strncpy(loc, line + 9, sizeof(loc));

                printf("loc=%s\n", loc);
            }

            if (strncmp(line, "model=", 6) == 0)
            {
                strncpy(mod, line + 6, sizeof(mod));

                printf("mod=%s\n", mod);
            }

            if (strncmp(line, "version=", 8) == 0)
            {
                strncpy(ver, line + 8, sizeof(ver));

                printf("ver=%s\n", ver);
            }

            if (strncmp(line, "uuid=", 5) == 0)
            {
                strncpy(uid, line + 5, sizeof(uid));

                printf("uid=%s\n", uid);
            }
        }

        fclose(fd);
    }
}

void putCustomInfo()
{
    FILE *fd = fopen("/etc/meme.txt", "wb");

    if (fd == NULL) fd = fopen("./meme.txt", "wb");

    if (fd != NULL)
    {
        if (strlen(nam)) fprintf(fd, "name=%s\n", nam);
        if (strlen(nck)) fprintf(fd, "nick=%s\n", nck);
        if (strlen(loc)) fprintf(fd, "location=%s\n", loc);
        if (strlen(mod)) fprintf(fd, "model=%s\n", mod);
        if (strlen(ver)) fprintf(fd, "version=%s\n", ver);
        if (strlen(uid)) fprintf(fd, "uuid=%s\n", uid);

        fclose(fd);
    }
}

void getCloudInfo()
{
    FILE *fd = fopen("/tmp/log.txt", "rb");

    if (fd == NULL) fd = fopen("./log.txt", "rb");

    if (fd != NULL)
    {
        char line[1024];

        while (fgets(line, sizeof(line), fd))
        {
            if (strncmp(line, "inpwd=", 6) == 0)
            {
                strncpy(dpw, line + 6, 15);

                printf("dpw=%s\n", dpw);

                break;
            }
        }

        fclose(fd);
    }
}

void getUUID()
{
    FILE *fd = fopen("/proc/sys/kernel/random/uuid", "rb");

    if (fd != NULL)
    {
        if (fgets(uid, sizeof(uid), fd) > 0)
        {
            strtrim(uid);
        }

        fclose(fd);
    }
    else
    {
        //
        // Insecure, only for testing on OSX.
        //
        // e5e6f68d-3b0c-4a5d-a766-146d31ffebad
        //

        srand((unsigned int) time(NULL));

        snprintf(uid, sizeof(uid), "%04x%04x-%04x-%04x-%04x-%04x%04x%04x",
                 rand() & 0xffff, rand() & 0xffff,
                 rand() & 0xffff,
                 rand() & 0xffff,
                 rand() & 0xffff,
                 rand() & 0xffff, rand() & 0xffff, rand() & 0xffff);
    }

    printf("uid=%s\n", uid);
}

void catMEME(char *str)
{
    strlcat(memebuff, str, MSGBUFSIZE);
}

void escMEME(char *str)
{
    int size = strlen(memebuff);

    for (int inx = 0; str[ inx ];  inx++)
    {
        if ((str[ inx ] == '"') || (str[ inx ] == '\\'))
        {
            if ((size + 1) < MSGBUFSIZE) memebuff[ size++ ] = '\\';
            if ((size + 1) < MSGBUFSIZE) memebuff[ size++ ] = str[ inx ];
            continue;
        }
        else
        {
            if (str[ inx ] == '\t')
            {
                if ((size + 1) < MSGBUFSIZE) memebuff[size++] = '\\';
                if ((size + 1) < MSGBUFSIZE) memebuff[size++] = 't';
                continue;
            }

            if (str[ inx ] == '\n')
            {
                if ((size + 1) < MSGBUFSIZE) memebuff[size++] = '\\';
                if ((size + 1) < MSGBUFSIZE) memebuff[size++] = 'n';
                continue;
            }

            if (str[ inx ] == '\r')
            {
                if ((size + 1) < MSGBUFSIZE) memebuff[size++] = '\\';
                if ((size + 1) < MSGBUFSIZE) memebuff[size++] = 'r';
                continue;
            }
        }

        if ((size + 1) < MSGBUFSIZE) memebuff[ size++ ] = str[ inx ];
    }

    memebuff[ size ] = 0;
}

void formatMEME()
{
    memset(memebuff, 0, MSGBUFSIZE);

    catMEME("{");
    catMEME("\n");

    catMEME("  \"type\": \"MEME\",");
    catMEME("\n");

    if (strlen(nam) > 0)
    {
        catMEME("  \"device_name\": \"");
        escMEME(nam);
        catMEME("\",");
        catMEME("\n");
    }

    if (strlen(nck) > 0)
    {
        catMEME("  \"device_nick\": \"");
        escMEME(nck);
        catMEME("\",");
        catMEME("\n");
    }

    if (strlen(loc) > 0)
    {
        catMEME("  \"device_location\": \"");
        escMEME(loc);
        catMEME("\",");
        catMEME("\n");
    }

    if (strlen(mod) > 0)
    {
        catMEME("  \"device_model\": \"");
        escMEME(mod);
        catMEME("\",");
        catMEME("\n");
    }

    if (strlen(ver) > 0)
    {
        catMEME("  \"device_version\": \"");
        escMEME(ver);
        catMEME("\",");
        catMEME("\n");
    }

    catMEME("  \"p2p_id\": \"");
    escMEME(did);
    catMEME("\",");
    catMEME("\n");

    catMEME("  \"p2p_pw\": \"");
    escMEME(dpw);
    catMEME("\",");
    catMEME("\n");

    catMEME("  \"cloud_id\": \"");
    escMEME(cid);
    catMEME("\",");
    catMEME("\n");

    catMEME("  \"cloud_pw\": \"");
    escMEME(cpw);
    catMEME("\"");
    catMEME("\n");

    catMEME("}");
}

void responder()
{
    struct sockaddr_in addr;
    socklen_t addrlen = sizeof(addr);
    struct ip_mreq mreq;
    int yes = 1;
    int sockfd;

    if ((sockfd = socket(AF_INET, SOCK_DGRAM, 0)) < 0)
    {
        perror("Create socket failed.");
        return;
    }

    if (setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, &yes, sizeof(yes)) < 0)
    {
        perror("Reusing Addr failed.");
        return;
    }

    memset(&addr, 0, sizeof(addr));

    addr.sin_family = AF_INET;
    addr.sin_addr.s_addr = htonl(INADDR_ANY);
    addr.sin_port = htons(HELO_PORT);

    if (bind(sockfd, (struct sockaddr *) &addr, sizeof(addr)) < 0)
    {
        perror("Bind to socket failed.");
        return;
    }

    mreq.imr_multiaddr.s_addr = inet_addr(HELO_GROUP);
    mreq.imr_interface.s_addr = htonl(INADDR_ANY);

    if (setsockopt(sockfd, IPPROTO_IP, IP_ADD_MEMBERSHIP, &mreq, sizeof(mreq)) < 0)
    {
        perror("Join multicast failed.");
        return;
    }

    while (! exitloop)
    {
        memset(messbuff, 0, sizeof(messbuff));

        if (recvfrom(sockfd, messbuff, MSGBUFSIZE, 0, (struct sockaddr *) &addr, &addrlen) < 0)
        {
            perror("Receive from socket failed.");
            continue;
        }

        // puts(messbuff);

        char *type = jsonGetStringValue(messbuff, "type");
        char *name = jsonGetStringValue(messbuff, "devicename");
        if (name == NULL) name = jsonGetStringValue(messbuff, "device_name");

        printf("msg: %s => %s\n", type, name);

        if ((type != NULL) && (strcmp(type, "HELO") == 0))
        {
            formatMEME();

            if (sendto(sockfd, memebuff, strlen(memebuff), 0, (struct sockaddr *) &addr,
                       sizeof(addr)) < 0)
            {
                perror("Send to socket failed.");
                continue;
            }
        }

        if (type != NULL) free(type);
        if (name != NULL) free(name);
    }
}

int main(int argc, char *argv[])
{
    memset(nam, 0, sizeof(nam));
    memset(nck, 0, sizeof(nck));
    memset(loc, 0, sizeof(loc));
    memset(mod, 0, sizeof(mod));
    memset(ver, 0, sizeof(ver));
    memset(uid, 0, sizeof(uid));

    memset(did, 0, sizeof(did));
    memset(dpw, 0, sizeof(dpw));

    memset(cid, 0, sizeof(cid));
    memset(cpw, 0, sizeof(cpw));

    getCustomInfo();

    getHackInfo();
    getDeviceInfo();
    getCloudInfo();

    if (strlen(uid) == 0)
    {
        getUUID();

        putCustomInfo();
    }

    formatMEME();

    puts(memebuff);

    responder();

    return 0;
}
