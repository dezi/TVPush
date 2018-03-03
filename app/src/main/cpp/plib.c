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

            /*
            int bevor1 = *(data + inx - 2);
            int bevor2 = *(data + inx - 1);
            int after1 = *(data + inx + reallen);
            int after2 = *(data + inx + reallen + 1);

            printf("fund aaa %08dd %4d %s => %s %02x (%c) %02x -- %02x %02x (%c)\n", inx, (int) reallen, data + inx, suffix,
                   bevor1, bevor1, bevor2, after1, after2, after2);
            */

            printf("fund %08d %4d %s\n", inx, (int) reallen, target);

            memset(data + inx, 0, reallen);
            strcpy(data + inx, patchs);
            strcat(data + inx, suffix);

            /*
            bevor1 = *(data + inx - 2);
            bevor2 = *(data + inx - 1);
            after1 = *(data + inx + reallen);
            after2 = *(data + inx + reallen + 1);

            printf("fund bbb %08dd %4d %s => %s %02x (%c) %02x -- %02x %02x (%c)\n", inx, (int) reallen, data + inx, suffix,
                   bevor1, bevor1, bevor2, after1, after2, after2);
            */
        }
    }
}

void zz_top_p2p_api()
{
    char* inpLib = "libPPPP_API.so";
    char* outLib = "libzztopp2z.so";

    char inpPath[ 256 ];

    strcpy(inpPath, "/Users/dezi/TVPush/gen/");
    strcat(inpPath, inpLib);

    char outPath[ 256 ];
    strcpy(outPath, "/Users/dezi/TVPush/app/src/main/jnilibs/armeabi-v7a/");
    strcat(outPath, outLib);

    char cpyPath[ 256 ];
    strcpy(cpyPath, "/Users/dezi/TVPush/gen/");
    strcat(cpyPath, outLib);

    char strings[ 256 ];
    strcpy(strings, "strings");
    strcat(strings, " ");
    strcat(strings, cpyPath);
    strcat(strings, " > ");
    strcat(strings, cpyPath);
    strcat(strings, ".txt");

    long size;
    char *data = readlib(inpPath, &size);

    replacedat(data, size,
               "Java_com_p2p_pppp_1api_PPPP_1APIs_PPPP_1",
               "Java_com_xxx_yyyy_1aaa_XYZZ_1XXXX_HACK_1");

    replacedat(data, size,
               "PPPP_",
               "PPPP_");

    replacedat(data, size,
               inpLib,
               outLib);

    writelib(outPath, data, size);
    writelib(cpyPath, data, size);

    system(strings);
}

void zz_top_aac_api()
{
    char* inpLib = "libfaad.so";
    char* outLib = "libzztopaac.so";

    char inpPath[ 256 ];

    strcpy(inpPath, "/Users/dezi/TVPush/gen/");
    strcat(inpPath, inpLib);

    char outPath[ 256 ];
    strcpy(outPath, "/Users/dezi/TVPush/app/src/main/jnilibs/armeabi-v7a/");
    strcat(outPath, outLib);

    char cpyPath[ 256 ];
    strcpy(cpyPath, "/Users/dezi/TVPush/gen/");
    strcat(cpyPath, outLib);

    char strings[ 256 ];
    strcpy(strings, "strings");
    strcat(strings, " ");
    strcat(strings, cpyPath);
    strcat(strings, " > ");
    strcat(strings, cpyPath);
    strcat(strings, ".txt");

    long size;
    char *data = readlib(inpPath, &size);

    replacedat(data, size,
               "Java_com_aac_utils_DecodeAAC_nOpen",
               "Java_zz_top_aac_AACDecode_open");

    replacedat(data, size,
               "Java_com_aac_utils_DecodeAAC_nDecode",
               "Java_zz_top_aac_AACDecode_decode");

    replacedat(data, size,
               "Java_com_aac_utils_DecodeAAC_nClose",
               "Java_zz_top_aac_AACDecode_close");

    /*
    replacedat(data, size,
               inpLib,
               outLib);
    */

    writelib(outPath, data, size);
    writelib(cpyPath, data, size);

    system(strings);
}

int main(int argc, char *argv[])
{
    //zz_top_p2p_api();
    zz_top_aac_api();

    return 0;
}