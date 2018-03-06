//
// Poor man's json.
//

#include <string.h>
#include <stdlib.h>
#include <stdio.h>

#define DBG 0
#define BS 1024

char *parseDat(const char json[], const char *path);
char *parseQuoted(const char json[], int *jnx);
char *parseUnQuoted(const char json[], int *jnx);
void  parseSpace(const char json[], int *jnx);

int isTerminator(char ccc);
int isSpace(char ccc);

char *jsonGetStringValue(const char json[], const char path[])
{
    char *result = parseDat(json, path);

    return result;
}

char *parseDat(const char json[], const char *path)
{
    int jnx = 0;
    char ccc;

    char *cpth = malloc(BS);
    char *fund = NULL;
    char *last = NULL;

    strcpy(cpth, "");

    while (json[jnx])
    {
        parseSpace(json, &jnx);

        ccc = json[jnx++];

        if ((ccc == '{') || (ccc == '['))
        {
            continue;
        }

        if ((ccc == '}') || (ccc == ']') || (ccc == ','))
        {
            //
            // Pop last key from path.
            //

            int inx = strlen(cpth) - 1;

            while ((inx >= 0) && cpth[ inx ] != '|')
            {
                cpth[ inx-- ] = 0;
            }

            if (inx > 0)
            {
                //
                // Pop trailing bar.
                //
                
                cpth[ inx ] = 0;
            }

            continue;
        }

        if (ccc == ':')
        {
            if (last != NULL)
            {
                //
                // Push last key onto path.
                //

                if (strlen(cpth) > 0) strncat(cpth, "|", BS);

                strncat(cpth, last, BS);

                free(last);
                last = NULL;

                printf("######## %s\n", cpth);
            }

            continue;
        }

        if (ccc == '"')
        {
            if (last != NULL) free(last);
            last = parseQuoted(json, &jnx);
        }
        else
        {
            if (last != NULL) free(last);
            last = parseUnQuoted(json, &jnx);
        }

        if (DBG) puts(last);

        if (strcmp(path, cpth) == 0)
        {
            fund = last;
            last = NULL;

            break;
        }
    }

    if (last != NULL) free(last);

    free(cpth);

    return fund;
}

char *parseQuoted(const char json[], int *jnx)
{
    char *buffer;
    size_t size;
    char ccc;
    int tnx;

    tnx = *jnx;

    size = 0;

    while (((ccc = json[tnx++]) != '\"') && ccc)
    {
        if (ccc == '\\')
        {
            ccc = json[tnx++];
            if (! ccc) break;
        }

        size++;
    }

    tnx = *jnx;

    buffer = malloc(size + 1);

    size = 0;

    while (((ccc = json[tnx++]) != '\"') && ccc)
    {
        if (ccc == '\\')
        {
            ccc = json[tnx++];
            if (! ccc) break;

            if (ccc == 'n') ccc = '\n';
            if (ccc == 'r') ccc = '\r';
            if (ccc == 't') ccc = '\t';
        }

        buffer[ size++ ] = ccc;
    }

    buffer[ size ] = 0;

    *jnx = tnx;

    return buffer;
}

char *parseUnQuoted(const char json[], int *jnx)
{
    char *buffer;
    size_t size;
    char ccc;
    int tnx;

    tnx = *jnx;

    size = 0;

    while ((! isTerminator(ccc = json[tnx++])) && ccc)
    {
        if (ccc == '\\')
        {
            ccc = json[tnx++];
            if (! ccc) break;
        }

        size++;
    }

    tnx = *jnx;

    buffer = malloc(size + 1);

    size = 0;

    while ((! isTerminator(ccc = json[tnx++])) && ccc)
    {
        if (ccc == '\\')
        {
            ccc = json[tnx++];
            if (! ccc) break;

            if (ccc == 'n') ccc = '\n';
            if (ccc == 'r') ccc = '\r';
            if (ccc == 't') ccc = '\t';
        }

        buffer[ size++ ] = ccc;
    }

    buffer[ size ] = 0;

    *jnx = tnx;

    return buffer;
}

void parseSpace(const char json[], int *jnx)
{
    while (isSpace(json[*jnx]))
    {
        (*jnx)++;
    }
}

int isSpace(char ccc)
{
    return (ccc == ' ') || (ccc == '\t') || (ccc == '\r') || (ccc == '\n');
}

int isTerminator(char ccc)
{
    return isSpace(ccc) ||
           (ccc == '{') || (ccc == '}') ||
           (ccc == '[') || (ccc == ']') ||
           (ccc == ',') || (ccc == ':');
}
