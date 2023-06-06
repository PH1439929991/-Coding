#pragma once
#include<stdio.h>
#include<stdlib.h>
#include<string.h>

#define MAX 128
#define e 1        //e
#define FootLoc(p) (p) + (p)->size - 1
#define Max_Segment 10
#define LIST_INIT_SIZE 10

//内存字
typedef struct WORD
{
	union {
		WORD* llink;
		WORD* uplink;
	} a;
	int tag;
	int size;
	WORD* rlink;
} WORD, head, foot, *Space;
typedef struct
{
	Space s; //占用块指针循环队列(初始化为空)，配合全局变量number实现
	int num; //进程编号
} S_Space;
//段
typedef struct Segment
{
	WORD* start; //物理段起始地址
	int length; // 段长
	int status; //是否在内存中，0表示不在，1表示在
	int time; //存储进程进入时间
} Segment;
//进程
typedef struct Process
{
	int ID;     //进程号
	Segment seg[Max_Segment]; //seg中存储的是每段的信息,每个进程最多分配10个段
	char name[20]; //进程名
	int seg_size;   //进程包含的段数
	int has_allocated; //已分配内存的段数
}Process;

void showMemory(Space &pav);//显示内存图
void initMemory(Space& pav);//初始化内存
Space AllocBoundTag(Space& pav, int n); //分配内存
void Reclaim(Space& pav, Space p);//回收内存
void initProcess();//初始化进程
int CreateProcess(char name[20], int segnum, int segsize[]);//创建进程
bool FIFO(Space& pav, Space& p);//替换算法，FIFO
WORD* PrintAddress(Space& pav, Space& a, int processId, int segId, int offset); //地址映射
WORD* MissSegInt(Space& pav, Space& a, int processId, int segId, int offset); //缺段中断处理