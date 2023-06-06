// OsLib.cpp : 定义静态库的函数。
//
#include"OsLib.h"
int entertime = 0; //全局变量，用于表示进程进入的时间序列
int SIZE = 0;
int InterruptType = -1; //中断类型，1代表越界中断，2代表缺段中断,3代表内存满中断（会采用FIFO）
Process* A;
S_Space v[MAX / e] = { NULL }; //循环队列
int front = 0;                 //记录队列首元素位置
int number = 0;                //全局变量，第number个进入内存的段
int NUMBER = 0;
// TODO: 这是一个库函数示例
//替换算法,FIFO
bool FIFO(Space& pav, Space& p)
{ //淘汰算法，可利用队列v的队头元素（FIFO），再与存储所有进程信息中的A.seg[i].start进行比较，相同则对其中信息进行修改
    for (int i = 0; i < A[v[front].num].seg_size; i++) {
		if (A[v[front].num].seg[i].start == v[front].s)
		{
			A[v[front].num].seg[i].status = 0;
			A[v[front].num].seg[i].start = NULL;
			SIZE = SIZE - A[v[front].num].seg[i].length;
			p = A[v[front].num].seg[i].start;
			A[v[front].num].has_allocated--;
			break;
		}
	}
	////回收空间
	p = v[front].s;
	Reclaim(pav, p);
	front++;
	return true;
}

//地址映射
WORD* PrintAddress(Space& pav, Space& a, int processId, int segId, int offset)
{
	//重置中断类型
	InterruptType = -1;
	WORD* q;
	if (offset > A[processId].seg[segId].length) {
		//发生越界
		InterruptType = 1;
		return NULL;
	}
	else {
		if (A[processId].seg[segId].status) //该段在内存中
		{
			q = A[processId].seg[segId].start + offset * sizeof(WORD);
			front++;
			v[number % (MAX / e)].s = A[processId].seg[segId].start;
			v[number % (MAX / e)].num = processId;
			number++;
		}
		else //该段不在内存中
		{
			//缺段中断类型
			InterruptType = 2;
			//缺段中断处理
			q = MissSegInt(pav, a, processId, segId, offset);
		}
		return q;
	}
}

//缺段中断处理
WORD* MissSegInt(Space& pav, Space& a, int processId, int segId, int offset) {
	WORD* q;
	for (; ; )
		if (SIZE + A[processId].seg[segId].length <= MAX)
		{
			//返回NULL 分配成功 
			//返回地址 则是pav指向表中刚分配过的结点的后继结点
			a = AllocBoundTag(pav, A[processId].seg[segId].length);
			if (!a) {
				InterruptType = 3;
				FIFO(pav, a);
			}
			else {
				SIZE += A[processId].seg[segId].length;
				A[processId].has_allocated++;
				A[processId].seg[segId].start = a;
				v[number % (MAX / e)].s = A[processId].seg[segId].start;
				v[number % (MAX / e)].num = processId;
				A[processId].seg[segId].status = 1;
				A[processId].seg[segId].time = entertime;
				entertime++;
				q = A[processId].seg[segId].start + offset * sizeof(WORD);
				number++;
				return q;
			}
		}
		else {
			InterruptType = 3;
			FIFO(pav, a);
		}
}

//初始化内存
void initMemory(Space& pav)
{
	Space p;
	p = (WORD*)malloc((MAX + 2) * sizeof(WORD));
	p->tag = 1; //设置低址边界，以防查找左右邻块时出错
	pav = p + 1;
	pav->rlink = pav->a.llink = pav;
	pav->tag = 0;
	pav->size = MAX;
	p = FootLoc(pav);
	p->a.uplink = pav;
	p->tag = 0;
	(p + 1)->tag = 1; //设置高址边界，以防查找左右邻块时出错
}


//分配内存
Space AllocBoundTag(Space& pav, int n)
{ 
	/* 若有不小于n的空闲块,则分配相应的存储块,并返回其首地址;否则返回NULL */
	/* 若分配后可利用空间表不空，则pav指向表中刚分配过的结点的后继结点 */
	Space p, f;
	for (p = pav; p && p->size < n && p->rlink != pav; p = p->rlink)
		;
	if (!p || p->size < n)
		return NULL;
	else
	{
		f = FootLoc(p);
		pav = p->rlink;
		if (p->size - n <= e)
		{
			if (pav == p)
				pav = NULL;
			else
			{
				pav->a.llink = p->a.llink;
				p->a.llink->rlink = pav;
			}
			p->tag = f->tag = 1;
		}
		else
		{
			f->tag = 1;
			p->size -= n;
			f = FootLoc(p);
			f->tag = 0;
			f->a.uplink = p;
			p = f + 1;
			p->tag = 1;
			p->size = n;
		}
		return p;
	}
}
//回收内存
void Reclaim(Space& pav, Space p)
{ //边界标识法的回收算法,s、t分别指向释放块的左、右邻块(空闲时)的首地址
	Space s = (p - 1)->a.uplink, t = p + p->size;
	int l = (p - 1)->tag, r = (p + p->size)->tag; 
	/* l、r分别指示释放块的左、右邻块是否空闲 */
	if (!pav)// 可利用空间表空
	{
		pav = p->a.llink = p->rlink = p;
		p->tag = 0;
		(FootLoc(p))->a.uplink = p;
		(FootLoc(p))->tag = 0;
	}
	else //可利用空间表不空
	{
		if (l == 1 && r == 1) //左右邻区均为占用块
		{
			p->tag = 0;
			(FootLoc(p))->a.uplink = p;
			(FootLoc(p))->tag = 0;
			pav->a.llink->rlink = p;
			p->a.llink = pav->a.llink;
			p->rlink = pav;
			pav->a.llink = p;
			pav = p;
		}
		else if (l == 0 && r == 1) // 左邻区为空闲块,右邻区为占用块,合并左邻块和释放块
		{
			s = (p - 1)->a.uplink;
			s->size += p->size;
			t = FootLoc(p);
			t->a.uplink = s;
			t->tag = 0;
		}
		else if (l == 1 && r == 0) //右邻区为空闲块,左邻区为占用块,合并右邻块和释放块
		{
			p->tag = 0;
			p->a.llink = t->a.llink;
			p->a.llink->rlink = p;
			p->rlink = t->rlink;
			p->rlink->a.llink = p;
			p->size += t->size;
			(FootLoc(t))->a.uplink = p;
			if (pav == t)
				pav = p;
		}
		else //左右邻区均为空闲块
		{
			s->size += p->size + t->size;
			t->a.llink->rlink = t->rlink;
			t->rlink->a.llink = t->a.llink;
			(FootLoc(t))->a.uplink = s;
			if (pav == t)
				pav = s;
		}
	}
	p = NULL; //令刚释放的结点的指针为空
}

//初始化进程
void initProcess() {
	A = (Process*)malloc(LIST_INIT_SIZE * sizeof(Process));
	for (int i = 0; i < LIST_INIT_SIZE; i++) {
		A[i].ID = -1;
		A[i].seg_size = -1;
		A[i].has_allocated = -1;
		for (int j = 0; j < Max_Segment; j++) {
			A[i].seg[j].status = 0;
			A[i].seg[j].length = -1;
			A[i].seg[j].time = -1;
			A[i].seg[j].start = NULL;
		}
	}
}

//显示内存模块
void showMemory(Space &pav) {
	for (int i = 0; i < MAX + 2; i++) {
		if (i % 10 == 0 && i > 0) printf("\n");
		if ((pav + i)->tag == 1 || (pav + i)->tag == 0) {
			printf("%d ", (pav + i)->tag);
		}
		else printf("-1 ");
	}
	for (int i = front; i < MAX/e; i++) {
		printf("物理地址为:%12u ", (v + i)->s);
	}
}


//创建进程,返回值:1代表进程创建成功，2代表进程段数大于10
int CreateProcess(char name[20], int segnum, int segsize[])
{ //接收多个进程及进程信息
	//如果申请的段数大于10，返回2
	if (segnum > 10)
		return 2;

	A[NUMBER].ID = NUMBER;
	strcpy_s(A[NUMBER].name, name);
	A[NUMBER].seg_size = segnum;

	for (int i = 0; i < A[NUMBER].seg_size; i++)
	{
		A[NUMBER].seg[i].length = segsize[i];
	}
	NUMBER++;
	//进程创建成功，返回1
	return 1;
}
