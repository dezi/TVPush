//
// Poor man's json.
//

#include <string.h>
#include <stdlib.h>
#include <stdio.h>

#include "json.h"

char *parseQuoted(const char json[], int *jnx);
char *parseUnQuoted(const char json[], int *jnx);
char *parseDat(const char json[], const char *path);

char *getStringValue(const char json[], const char path[])
{
    char *result = parseDat(json, path);

    return result;
}

int isSpace(char ccc)
{
    return (ccc == ' ') || (ccc == '\t') || (ccc == '\r') || (ccc == '\n');
}

void skipSpace(const char json[], int *jnx)
{
    while (isSpace(json[*jnx]))
    {
        (*jnx)++;
    }
}

char *parseDat(const char json[], const char *path)
{
    int jnx = 0;
    char ccc;
    char *last = NULL;

    while (json[jnx])
    {
        skipSpace(json, &jnx);

        ccc = json[jnx++];

        if (ccc == '{')
        {
            continue;
        }

        if (ccc == '}')
        {
            continue;
        }

        if (ccc == '[')
        {
            continue;
        }

        if (ccc == ']')
        {
            continue;
        }

        if (ccc == ':')
        {
            continue;
        }

        if (ccc == ',')
        {
            continue;
        }

        if (ccc == '"')
        {
            last = parseQuoted(json, &jnx);
        }
        else
        {
            last = parseUnQuoted(json, &jnx);
        }

        puts(last);
    }

    return NULL;
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

    while ((! isSpace(ccc = json[tnx++])) && ccc)
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

    while ((! isSpace(ccc = json[tnx++])) && ccc)
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

