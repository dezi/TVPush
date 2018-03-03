#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <time.h>

#define true 1
#define false 0
#define null NULL

char *readlib(char *name, long *size)
{
    FILE *fd = fopen(name, "rb");
    if (fd == null) return null;

    fseek(fd, 0, SEEK_END);
    *size = (long) ftell(fd);
    fseek(fd, 0, SEEK_SET);

    printf("size %ld\n", *size);

    char *data = malloc((size_t) *size + 1);
    data[ *size ] = 0;

    long xfer = fread(data, 1, (size_t) *size, fd);

    printf("xfer %ld\n", xfer);

    fclose(fd);

    return data;
}

void writelib(char *name, char *data, long size)
{
    FILE *fd = fopen(name, "wb");
    if (fd == null) return;

    fwrite(data, 1, size, fd);
    fclose(fd);
}
void replacedat(char *data, long size, char *target, char *patchs)
{
    char suffix[ 256 ];

    size_t tarlen = strlen(target);

    for (int inx = 0; inx < size - 100; inx++)
    {
        if (strncmp(data + inx, target, tarlen) == 0)
        {
            size_t reallen = strlen(data + inx);
            strcpy(suffix, data + inx + tarlen);

            int bevor1 = *(data + inx - 2);
            int bevor2 = *(data + inx - 1);
            int after1 = *(data + inx + reallen);
            int after2 = *(data + inx + reallen + 1);

            printf("fund aaa %d %d %s => %s %02x (%c) %02x -- %02x %02x (%c)\n", inx, (int) reallen, data + inx, suffix,
                   bevor1, bevor1, bevor2, after1, after2, after2);

            memset(data + inx, 0, reallen);
            strcpy(data + inx, patchs);
            strcat(data + inx, suffix);

            bevor1 = *(data + inx - 2);
            bevor2 = *(data + inx - 1);
            after1 = *(data + inx + reallen);
            after2 = *(data + inx + reallen + 1);

            printf("fund bbb %d %d %s => %s %02x (%c) %02x -- %02x %02x (%c)\n", inx, (int) reallen, data + inx, suffix,
                   bevor1, bevor1, bevor2, after1, after2, after2);
        }
    }
}

void zz_top_p2p_api()
{
    long size;
    char *data = readlib("/Users/dezi/TVPush/gen/libPPPP_API.so", &size);

    //replacedat(data, size,
    //           "Java_com_p2p_pppp_1api_PPPP_1APIs_PPPP_1",
    //           "Java_zz_top_p2p_api_P2PApiNativexxxxxxx_");

    replacedat(data, size,
               "Java_com_p2p_pppp_1api_PPPP_1APIs_PPPP_1GetAPIVersion",
               "Java_com_p2p_pppp_1api_PPPP_1APIs_PPPP_1GetAPIVersion");

    replacedat(data, size,
               "PPPP_GetAPIVersion",
               "PPPP_GetAPIVersion");

    writelib("/Users/dezi/TVPush/app/src/main/jnilibs/armeabi-v7a/libPPPP_API.so", data, size);

    writelib("/Users/dezi/TVPush/gen/libzztopp2p.so", data, size);

    system("strings /Users/dezi/TVPush/gen/libzztopp2p.so > /Users/dezi/TVPush/gen/libzztopp2p.txt");
}

int main(int argc, char *argv[])
{
    zz_top_p2p_api();

    return 0;
}