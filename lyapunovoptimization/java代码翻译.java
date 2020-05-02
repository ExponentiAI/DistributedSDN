//这是 C++代码
//获取数据API:(孙伟平)
//由学长给出的C++代码demo，翻译为java代码。主要作用是聚合，通过聚合各组员提供的API包括各AP实时数据，以及通过 lyapunov和utility计算各ap权重和效用，从而实现优异的AP分配策略！

#include <omnetpp.h>

using namespace omnetpp;

namespace fifo {

    class Switch : public cSimpleModule{
        private:
            cMessage *sendMessageEvent;
            int k = 0;
            int my_meth();
            int randomSend();
        public:
            int *qLength;
            Switch();
            virtual ~Switch();

        protected:
            simsignal_t dropSignal;
            int dropNum = 0;
            virtual void handleMessage(cMessage *msg) override;
            virtual void initialize() override;
    };

    Define_Module(Switch);

    void Switch::initialize() {
        dropSignal = registerSignal("drop");
        emit(dropSignal, dropNum);
    }

    Switch::Switch(){
        sendMessageEvent = nullptr;
        qLength = new int[3];

        qLength[0] = 0;
        qLength[1] = 0;
        qLength[2] = 0;

    }

    Switch::~Switch(){
        cancelAndDelete(sendMessageEvent);
        delete[] qLength;
    }

    void Switch::handleMessage(cMessage *msg){
        int port = 0;
        if (msg->getKind() == 2001) {
            port = my_meth();
            if (port == -1) {
                dropNum++;
                EV << "queue is full. delete msg. dropNum is " << dropNum << endl;
                emit(dropSignal, dropNum);
                delete msg;
            } else {
                EV << "send message to " << port << endl;
                send(msg, "out", port);
            }
        } else {
            if (msg->getArrivalGateId() == 1048577) {
                qLength[0] = msg->getKind();
            } else if (msg->getArrivalGateId() == 1048578) {
                qLength[1] = msg->getKind();
            } else {
                qLength[2] = msg->getKind();
            }
        }

    }

    int Switch::randomSend() {
        double q1Max = 300.0;
        double q2Max = 280.0;
        double q3Max = 250.0;

        if (k % 3 == 0) {
            k = 0;
        }
        if (k == 0 && qLength[0] >= q1Max) {
            k++;
            return -1;
        }
        if (k == 1 && qLength[1] >= q2Max) {
            k++;
            return -1;
        }
        if (k == 2 && qLength[2] >= q3Max) {
            k++;
            return -1;
        }

        return k++;
    }


    int Switch::my_meth() {
        double q1Max = 300.0;
        double q2Max = 280.0;
        double q3Max = 250.0;

        double q1 = qLength[0]*0.25*(qLength[0]*1.0/(qLength[0]+q1Max)*1.0);
        double q2 = qLength[1]*0.35*(qLength[1]*1.0/(qLength[1]+q2Max)*1.0);
        double q3 = qLength[2]*0.4*(qLength[2]*1.0/(qLength[2]+q3Max)*1.0);

        EV << qLength[0] << "-" << qLength[1] << "-" << qLength[2] << endl;
        EV << q1 << "-" << q2 << "-" << q3 << endl;

        if (qLength[0] < q1Max && qLength[1] < q2Max && qLength[2] < q3Max) {
            if (q1 <= q2 && q1 <= q3) {
                return 0;
            } else if (q2 <= q3) {
                return 1;
            } else {
                return 2;
            }
        } else if (qLength[0] >= q1Max && qLength[1] < q2Max && qLength[2] < q3Max) {
            if (q2 <= q3) {
                return 1;
            } else {
                return 2;
            }
        } else if (qLength[1] >= q2Max && qLength[0] < q1Max && qLength[2] < q3Max) {
            if (q1 <= q3) {
                return 0;
            } else {
                return 2;
            }
        } else if (qLength[0] < q1Max && qLength[1] < q2Max && qLength[2] >= q3Max) {
            if (q1 <= q2) {
                return 0;
            } else {
                return 1;
            }
        } else if (qLength[0] >= q1Max && qLength[1] >= q2Max && qLength[2] < q3Max) {
            return 2;
        } else if (qLength[0] >= q1Max && qLength[1] < q2Max && qLength[2] >= q3Max) {
            return 1;
        } else if (qLength[0] < q1Max && qLength[1] >= q2Max && qLength[2] >= q3Max) {
            return 0;
        }
        else {
            EV << "queue 0-1-2 is full." << endl;
            return -1;
        }

    }

};


//这是 java代码

package Switch;

// import cPlus.simsignal_t;
// import cPlus.cSimpleModule;
// import cPlus.cMessage;
// import cPlus.registerSignal;


public class Switch extends cSimpleModule {

    public static int[] qLength;

    protected int dropNum = 0;
    protected simsignal_t dropSignal;
    
    private static cMessage sendMessageEvent;
    private int k=0;
    
    public static void Switch(){
        sendMessageEvent = null;
        qLength[0] = 0;
        qLength[1] = 0;
        qLength[2] = 0;
    };
    protected void handleMessage(cMessage msg){
        int port = 0;
        if (msg.getKind() == 2001) {
            port = my_meth();
            if (port == -1) {
                dropNum++;
                System.out.println("queue is full. delete msg. dropNum is "+dropNum);
                emit(dropSignal, dropNum);
            } else {
                System.out.println("send message to "+port);
                send(msg, "out", port);
            }
        } else {
            if (msg.getArrivalGateId() == 1048577) {
                qLength[0] = msg.getKind();
            } else if (msg.getArrivalGateId() == 1048578) {
                qLength[1] = msg.getKind();
            } else {
                qLength[2] = msg.getKind();
            }
        }
    }
    protected void initialize(){
        dropSignal = registerSignal("drop");
        emit(dropSignal, dropNum);
    }

    private int randomSend(){
        double q1Max = 300.0;
        double q2Max = 280.0;
        double q3Max = 250.0;

        if (k % 3 == 0) {
            k = 0;
        }
        if (k == 0 && qLength[0] >= q1Max) {
            k++;
            return -1;
        }
        if (k == 1 && qLength[1] >= q2Max) {
            k++;
            return -1;
        }
        if (k == 2 && qLength[2] >= q3Max) {
            k++;
            return -1;
        }

        return k++;
    }

    private int my_meth(){
        double q1Max = 300.0;
        double q2Max = 280.0;
        double q3Max = 250.0;

        double q1 = qLength[0]*0.25*(qLength[0]*1.0/(qLength[0]+q1Max)*1.0);
        double q2 = qLength[1]*0.35*(qLength[1]*1.0/(qLength[1]+q2Max)*1.0);
        double q3 = qLength[2]*0.4*(qLength[2]*1.0/(qLength[2]+q3Max)*1.0);

        System.out.println(qLength[0] + "-" + qLength[1] + "-" + qLength[2]);
        System.out.println(q1 + "-" + q2 + "-" + q3);

        if (qLength[0] < q1Max && qLength[1] < q2Max && qLength[2] < q3Max) {
            if (q1 <= q2 && q1 <= q3) {
                return 0;
            } else if (q2 <= q3) {
                return 1;
            } else {
                return 2;
            }
        } else if (qLength[0] >= q1Max && qLength[1] < q2Max && qLength[2] < q3Max) {
            if (q2 <= q3) {
                return 1;
            } else {
                return 2;
            }
        } else if (qLength[1] >= q2Max && qLength[0] < q1Max && qLength[2] < q3Max) {
            if (q1 <= q3) {
                return 0;
            } else {
                return 2;
            }
        } else if (qLength[0] < q1Max && qLength[1] < q2Max && qLength[2] >= q3Max) {
            if (q1 <= q2) {
                return 0;
            } else {
                return 1;
            }
        } else if (qLength[0] >= q1Max && qLength[1] >= q2Max && qLength[2] < q3Max) {
            return 2;
        } else if (qLength[0] >= q1Max && qLength[1] < q2Max && qLength[2] >= q3Max) {
            return 1;
        } else if (qLength[0] < q1Max && qLength[1] >= q2Max && qLength[2] >= q3Max) {
            return 0;
        }
        else {
            System.out.println("queue 0-1-2 is full.");
            return -1;
        }
    }

    
}