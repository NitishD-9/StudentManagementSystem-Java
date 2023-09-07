

class Student {
    String name;
    int age;
    String course;
    int rollNo;
    String gender;
    String dateOfBirth;

    public Student(int rollNo, String name, int age, String gender, String dateOfBirth, String course) {
        this.rollNo = rollNo;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.course = course;
    }
}