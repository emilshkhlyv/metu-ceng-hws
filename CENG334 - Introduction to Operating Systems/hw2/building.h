#ifndef __CONTROL_H
#define __CONTROL_H

#include <iostream>
#include "monitor.h"
#include <cstdlib>
#include <vector>
#include <string>
#include <bits/stdc++.h> 

using namespace std;

#define IDLE    0
#define DOWN    1
#define UP      2

#define hp      2
#define lp      1


typedef struct person{
    int id, weight_person, initial_floor, destination_floor, priority, direction;
} person;

typedef struct elevator{
    int weight_capacity;
    int person_capacity;
    int travel_time;
    int idle_time;
    int in_out_time;
    int state;
    int floor;
    int inside_person;
    int inside_weight;
    vector<person>  inside_people;
    vector<int>     destination;
} elevator;

class building: public Monitor{
    private:
        int         num_floors, num_people, weight_capacity, person_capacity, travel_time, idle_time, in_out_time;
        elevator        Elevator;
        person*         People;
        vector<person>* katlar;
        Condition       sira_sende;
        Condition       kat_geldi;
        Condition       request_again;
        Condition       indim;
        int*        served;
        int*        inside;
    public:
        building(int n_f, int n_p, int w_c, int p_c, int t_t, int i_t, int i_o_t): sira_sende(this), kat_geldi(this), indim(this), request_again(this){
            num_floors                      = n_f;
            num_people                      = n_p;
            weight_capacity                 = w_c;
            person_capacity                 = p_c;
            travel_time                     = t_t;
            idle_time                       = i_t;
            in_out_time                     = i_o_t;
            Elevator.inside_person          = 0;
            Elevator.inside_weight          = 0;
            Elevator.floor                  = 0;
            Elevator.person_capacity        = p_c;
            Elevator.weight_capacity        = w_c;
            Elevator.travel_time            = t_t;
            Elevator.idle_time              = i_t;
            Elevator.in_out_time            = i_o_t;
            Elevator.state                  = IDLE;
            inside                          = (int*)malloc(sizeof(int)*n_p);
            served                          = (int*)malloc(sizeof(int)*n_p);
            for(int f = 0; f < n_p; ++f){
                served[f] = 0;
                inside[f] = 0;  
            }
            katlar                          = (vector<person>*) malloc(sizeof(vector<person>)*num_floors);
            People                          = (person*)malloc(sizeof(person)*num_people);
        }
        void elvator(){
            int simended = 0;
            while(!simended){
                while(Elevator.destination.empty() && !simended) {
                    jayko();
                    usleep(idle_time);
                    simended = sim_calc();
                }
                usleep(in_out_time);
                usleep(travel_time);
                start_move();
                simended = sim_calc();
            }
        }
        // ELEVATOR FUNCTIONS
        void start_move                 (){
            __synchronized__;
            if(Elevator.destination[0] > Elevator.floor){
                Elevator.state = UP;
                sort(Elevator.destination.begin(), Elevator.destination.end(), solar);
                ++(Elevator.floor);
            }
            else if(Elevator.destination[0] < Elevator.floor){
                Elevator.state = DOWN;
                sort(Elevator.destination.begin(), Elevator.destination.end(), solmaz);
                --(Elevator.floor);
            }
            kat_cikarma();
            if(Elevator.destination.empty()) 
            {
                Elevator.state = IDLE;
            }  
            int p = 1;
            for(int k = 0; k < num_people; ++k){
                if(served[k] == 0) p = 0;
            }
            if(!p) print_elevator_for_elevator();
            
            kat_geldi.notifyAll();
            request_again.notifyAll();
        }
        void print_elevator_for_elevator(){
            switch(Elevator.state){
                case UP:
                    cout << "Elevator (Moving-up, " << Elevator.inside_weight << ", " << Elevator.inside_person << ", " << Elevator.floor << " -> "; 
                    int yor;
                    for(yor = 0; yor < Elevator.destination.size()-1; ++yor){
                        cout << Elevator.destination[yor] << ", "; 
                    }
                    cout << Elevator.destination[yor] << ")"<< endl;
                    break;  
                case DOWN:
                    cout  << "Elevator (Moving-down";
                    cout << ", " << Elevator.inside_weight << ", " << Elevator.inside_person << ", " << Elevator.floor << " -> ";
                    int yer;
                    for(yer = 0; yer < Elevator.destination.size()-1; ++yer){
                        cout << Elevator.destination[yer] << ", "; 
                    }
                    cout << Elevator.destination[yer] << ")" << endl;
                    break;
                case IDLE:
                    cout << "Elevator (Idle, " << Elevator.inside_weight << ", " << Elevator.inside_person << ", " << Elevator.floor << " ->)" << endl;
                    break;
            }
        }
        void kat_cikarma                (){
            std::vector<int>::iterator keep;
            keep = find(Elevator.destination.begin(), Elevator.destination.end(), Elevator.floor);
            if(keep != Elevator.destination.end()) Elevator.destination.erase(find(Elevator.destination.begin(), Elevator.destination.end(), Elevator.floor));
        }
        void person_ekleme              (person binecek){
            std::vector<int>::iterator it;
            it = find(Elevator.destination.begin(), Elevator.destination.end(), binecek.destination_floor);
            if(it == Elevator.destination.end()) Elevator.destination.push_back(binecek.destination_floor);
        }
        // PERSON FUNCTIONS
        bool yarasa(person insan){
            __synchronized__;
            int able;
            able = make_request(insan);
            if(able != -1){
                wait_initial_floor(insan);
                while(inecek_var()) indim.wait();
                bin(insan);
                if(inside[insan.id] == 1){
                    waiting_for_destination(insan);
                    in(insan);
                    return true;
                }
            }
            request_again.wait();
            return false;
        }
        int  make_request                   (person p)              {
            if (Elevator.state == IDLE){
                if(Elevator.floor == p.initial_floor){
                    print_person_request(p);
                    katlar[p.initial_floor].push_back(p);
                    sort(katlar[p.initial_floor].begin(), katlar[p.initial_floor].end(), sortas);
                    print_elevator_after_request();
                    return IDLE;
                }
                else if(katlar[Elevator.floor].size() == 0){ 
                    print_person_request(p);
                    katlar[p.initial_floor].push_back(p);
                    std::vector<int>::iterator it;
                    it = find(Elevator.destination.begin(), Elevator.destination.end(), p.initial_floor);
                    if(it == Elevator.destination.end()) Elevator.destination.push_back(p.initial_floor); 
                    sort(katlar[p.initial_floor].begin(), katlar[p.initial_floor].end(), sortas);
                    if(p.initial_floor > Elevator.floor)        Elevator.state = UP;
                    else if (p.initial_floor < Elevator.floor)  Elevator.state = DOWN;
                    print_elevator_after_request();
                    return IDLE;
                }
                return -1;
            }
            else if (Elevator.state == DOWN  && p.direction == DOWN  && p.initial_floor <= Elevator.floor){
                print_person_request(p);
                std::vector<int>::iterator it;
                it = find(Elevator.destination.begin(), Elevator.destination.end(), p.initial_floor);
                if(it == Elevator.destination.end() && Elevator.floor != p.initial_floor) Elevator.destination.push_back(p.initial_floor); 
                sort(Elevator.destination.begin(), Elevator.destination.end(), solmaz);
                katlar[p.initial_floor].push_back(p);
                sort(katlar[p.initial_floor].begin(), katlar[p.initial_floor].end(), sortas);
                print_elevator_after_request();
                return DOWN;
            }
            else if (Elevator.state == UP    && p.direction == UP    && p.initial_floor >= Elevator.floor){
                print_person_request(p);
                std::vector<int>::iterator it;
                it = find(Elevator.destination.begin(), Elevator.destination.end(), p.initial_floor);
                if(it == Elevator.destination.end() && Elevator.floor != p.initial_floor) Elevator.destination.push_back(p.initial_floor); 
                sort(Elevator.destination.begin(), Elevator.destination.end(), solar);
                katlar[p.initial_floor].push_back(p);
                sort(katlar[p.initial_floor].begin(), katlar[p.initial_floor].end(), sortas);
                print_elevator_after_request();
                return UP;
            }
            return -1;
        }
        void print_person_request           (person p)              {
            cout << "Person (" << p.id << ", ";
            string pri = (p.priority == lp) ? "lp" : "hp";
            cout << pri << ", " << p.initial_floor << " -> " << p.destination_floor << ", " << p.weight_person << ") made a request" << endl;
        }
        void print_elevator_after_request   ()                      {
            if(Elevator.state == UP){
                sort(Elevator.destination.begin(), Elevator.destination.end(), solar);
                cout << "Elevator (Moving-up, " << Elevator.inside_weight << ", " << Elevator.inside_person << ", " << Elevator.floor << " -> "; 
                int yer;
                for(yer = 0; yer < Elevator.destination.size()-1; ++yer){
                    cout << Elevator.destination[yer] << ", "; 
                }
                cout << Elevator.destination[yer] << ")"<< endl;
            }
            else if (Elevator.state == DOWN){
                sort(Elevator.destination.begin(), Elevator.destination.end(), solmaz);
                cout  << "Elevator (Moving-down";
                cout << ", " << Elevator.inside_weight << ", " << Elevator.inside_person << ", " << Elevator.floor << " -> ";
                int yer;
                for(yer = 0; yer < Elevator.destination.size()-1; ++yer){
                    cout << Elevator.destination[yer] << ", "; 
                }
                cout << Elevator.destination[yer] << ")" << endl;
            }
            else if (Elevator.state == IDLE){
                cout << "Elevator (Idle, " << Elevator.inside_weight << ", " << Elevator.inside_person << ", " << Elevator.floor << " ->)" << endl; 
            }
        }
        void wait_initial_floor(person p){
            while(p.initial_floor != Elevator.floor){
                kat_geldi.wait();
            }
        }
        void bin(person binecek){
            kat_cikarma();
            if(katlar[Elevator.floor].size() != 0){
                while(binecek.id != katlar[Elevator.floor][0].id) {
                    sira_sende.wait();
                }
                if(Elevator.person_capacity != Elevator.inside_person && Elevator.weight_capacity - Elevator.inside_weight >= binecek.weight_person){
                    if(Elevator.state == IDLE){
                        Elevator.inside_people.push_back(binecek);
                        inside[binecek.id] = 1;
                        string pri;
                        pri = (binecek.priority == hp) ? "hp" : "lp";
                        cout << "Person (" << binecek.id << ", " << pri << ", " << binecek.initial_floor << " -> " << binecek.destination_floor << ", " << binecek.weight_person << ") entered the elevator" << endl;
                        Elevator.inside_weight += binecek.weight_person;    
                        ++(Elevator.inside_person);  
                        person_ekleme(binecek);
                        if(binecek.destination_floor > Elevator.floor) Elevator.state = UP;
                        else Elevator.state = DOWN;
                        print_elevator_for_elevator();
                    }
                    else if(Elevator.state == binecek.direction){
                        Elevator.inside_people.push_back(binecek);
                        inside[binecek.id] = 1;
                        string pri;
                        pri = (binecek.priority == hp) ? "hp" : "lp";
                        cout << "Person (" << binecek.id << ", " << pri << ", " << binecek.initial_floor << " -> " << binecek.destination_floor << ", " << binecek.weight_person << ") entered the elevator" << endl;
                        Elevator.inside_weight += binecek.weight_person;    
                        ++(Elevator.inside_person);  
                        person_ekleme(binecek);
                        if(Elevator.state == UP) sort(Elevator.destination.begin(), Elevator.destination.end(), solar);
                        else if(Elevator.state == DOWN) sort(Elevator.destination.begin(), Elevator.destination.end(), solmaz);
                        print_elevator_for_elevator();
                    }
                }
                katlar[Elevator.floor].erase(katlar[Elevator.floor].begin());
            }
            sira_sende.notifyAll();
        }
        void in(person inecek){
            int icerdeki_yeri;
            for(int i = 0; i < Elevator.inside_people.size(); ++i){
                if(inecek.id == Elevator.inside_people[i].id) icerdeki_yeri = i;
            }
            Elevator.inside_people.erase(Elevator.inside_people.begin() + icerdeki_yeri);
            --(Elevator.inside_person);
            Elevator.inside_weight -= inecek.weight_person;
            string pri = (inecek.priority == hp) ? "hp" : "lp";
            cout << "Person (" <<  inecek.id << ", " << pri << ", " << inecek.initial_floor << " -> " << inecek.destination_floor << ", " << inecek.weight_person << ") has left the elevator" << endl;
            served[inecek.id] = 1;
            print_elevator_for_elevator();
            indim.notifyAll();
        }
        void waiting_for_destination(person p){
            while(Elevator.floor != p.destination_floor){
                kat_geldi.wait();
            }
        }
        void jayko(){
            __synchronized__;
            request_again.notifyAll();
        }
        bool inecek_var(){
            bool var = false;
            for(int i = 0; i < Elevator.inside_people.size(); ++i){
                if(Elevator.inside_people[i].destination_floor == Elevator.floor) var = true;
            }
            return var;
        }
        // SORT HELPER FUNCTIONS
        static bool solar   (int i3, int i4)        { 
            return (i3 < i4); }
        static bool solmaz  (int i1, int i2)        { 
            return (i1 > i2); }
        static bool sortas  (person p1, person p2)  { 
            return (p1.priority > p2.priority); }
        // PRIVATE ICIN FUNCTIONS
        int         sim_calc            (){
            __synchronized__;
            int simended = 1;
            for(int k = 0; k < num_people; ++k){
                if(served[k] == 0) simended = 0;
            }
            return simended;
        }
        person*     personlist          (){ 
            return this->People; }
        void        create_person       (int id, int w_p, int i_f, int d_f, int pri, int dir){
            People[id].id = id;
            People[id].weight_person = w_p;
            People[id].initial_floor = i_f;
            People[id].destination_floor = d_f;
            People[id].priority = pri;
            People[id].direction = dir;}
};
#endif