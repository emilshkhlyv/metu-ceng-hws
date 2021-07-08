all:
	g++ -g -O3 *.cpp -c -std=c++11 -march=native -ljpeg -lm
	g++ *.o -o raytracer -lpthread -ljpeg -lm

run:
	./raytracer hw2_sample_scenes/spheres_transform.xml
clean:
	rm -f *.o
