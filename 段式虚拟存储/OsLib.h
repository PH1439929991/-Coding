#pragma once
#include<stdio.h>
#include<stdlib.h>
#include<string.h>

#define MAX 128
#define e 1        //e
#define FootLoc(p) (p) + (p)->size - 1
#define Max_Segment 10
#define LIST_INIT_SIZE 10

//�ڴ���
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
	Space s; //ռ�ÿ�ָ��ѭ������(��ʼ��Ϊ��)�����ȫ�ֱ���numberʵ��
	int num; //���̱��
} S_Space;
//��
typedef struct Segment
{
	WORD* start; //�������ʼ��ַ
	int length; // �γ�
	int status; //�Ƿ����ڴ��У�0��ʾ���ڣ�1��ʾ��
	int time; //�洢���̽���ʱ��
} Segment;
//����
typedef struct Process
{
	int ID;     //���̺�
	Segment seg[Max_Segment]; //seg�д洢����ÿ�ε���Ϣ,ÿ������������10����
	char name[20]; //������
	int seg_size;   //���̰����Ķ���
	int has_allocated; //�ѷ����ڴ�Ķ���
}Process;

void showMemory(Space &pav);//��ʾ�ڴ�ͼ
void initMemory(Space& pav);//��ʼ���ڴ�
Space AllocBoundTag(Space& pav, int n); //�����ڴ�
void Reclaim(Space& pav, Space p);//�����ڴ�
void initProcess();//��ʼ������
int CreateProcess(char name[20], int segnum, int segsize[]);//��������
bool FIFO(Space& pav, Space& p);//�滻�㷨��FIFO
WORD* PrintAddress(Space& pav, Space& a, int processId, int segId, int offset); //��ַӳ��
WORD* MissSegInt(Space& pav, Space& a, int processId, int segId, int offset); //ȱ���жϴ���