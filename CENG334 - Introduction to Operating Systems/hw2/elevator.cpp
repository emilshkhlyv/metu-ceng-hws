#include <fstream>
#include <pthread.h>
#include <cstdlib>
#include <unistd.h>
#include "building.h"

using namespace std;

typedef struct argas{
    building *bs;
    int i;
} arguments;

void *elevator_thread(void *p){
    building* Building = (building*) p;
    int k = 0;
    Building->elvator();
}

void *person_func(void *p){
    arguments* argas = (arguments*) p;
    building* Building = argas->bs;
    int i = argas->i;
    person insan = Building->personlist()[i];
    while(1){
        if(Building->yarasa(insan)) break;
    }
}

int main(int argc, char *argv[])
{
    int i = 0, j = 0, k = 0;
    //General INFO vars
    static int num_floors, num_people, weight_capacity, person_capacity;
    static int travel_time, idle_time, in_out_time;

    //Personal INFO vars
    int weight_person, initial_floor, destination_floor, priority;
    //read input
    ifstream File;
    File.open(argv[1]);
    File >> num_floors >> num_people >> weight_capacity >> person_capacity >> travel_time >> idle_time >> in_out_time;
    
    building Building(num_floors, num_people, weight_capacity, person_capacity, travel_time, idle_time, in_out_time);
    person People[num_people];
    while(File >> weight_person >> initial_floor >> destination_floor >> priority){
        int direct = (destination_floor < initial_floor) ? DOWN : UP;
        Building.create_person(i, weight_person, initial_floor, destination_floor, priority, direct);
        ++i;
    }
    File.close();

    pthread_t* personals, eleval;
    personals = new pthread_t[num_people];

    arguments* args = new arguments[num_people];
    
    pthread_create(&eleval, NULL, elevator_thread, (void *) &Building);
    for(j = 0; j < num_people; ++j){
        args[j].bs = &Building;
        args[j].i = j;
        pthread_create(&personals[j], NULL, person_func, (void *) (args + j));
    }
    for(j = 0; j < num_people; ++j)
    {
        pthread_join(personals[j],NULL);
    }
    pthread_join(eleval, NULL);
    return 0;
}