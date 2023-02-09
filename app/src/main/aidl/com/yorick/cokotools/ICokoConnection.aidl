package com.yorick.cokotools;

interface ICokoConnection {

    int getVersion() = 2;

    int getUid() = 3;

    boolean checkSelfPermission() = 15;

    void destroy() = 16777114; // Destroy method defined by Shizuku server
}