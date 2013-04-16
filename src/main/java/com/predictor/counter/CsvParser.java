package com.predictor.counter;

import java.lang.Integer;import java.lang.Long;import java.lang.String;
public class CsvParser {
    private static final int ID_POSITION = 0;
    private static final int SEX_POSITION = 1;
    private static final int NAME_POSITION = 2;
    private static final int AGE_POSITION = 3;

    /**
     * Parses csv and creates an instance of Person <br>
     * csv format: NUM;SEX;NAME;AGE
     * @param csvLine csv line to parse
     * @return instance of Person
     * @throws CounterException in case of incorrect csv format
     */
    public static Person parseLine(String csvLine) throws CounterException {
        String [] values = csvLine.split(";");
        long id;
        Person.Sex sex;
        String name;
        int age;
        if(values.length<4){
            throw  new CounterException("Invalid csv format. Line ["+csvLine+"].");
        }
        for(int i=0; i<values.length; i++){
            values[i]=values[i].trim();
        }
        try{
            id = Long.parseLong(values[ID_POSITION]);
        } catch (java.lang.NumberFormatException e){
            throw new CounterException(("Id ["+values[ID_POSITION]+"] is not a number."));
        }
        if(values[SEX_POSITION].equalsIgnoreCase("male")){
            sex = Person.Sex.MALE;
        }
        else if(values[SEX_POSITION].equalsIgnoreCase("female")){
            sex = Person.Sex.FEMALE;
        }
        else {
            throw  new CounterException("["+values[SEX_POSITION]+"] is not a valid sex value.");
        }
        name = values[NAME_POSITION];
        try{
            age = Integer.parseInt(values[AGE_POSITION]);
        }catch (java.lang.NumberFormatException e){
            throw new CounterException(("Age ["+values[AGE_POSITION]+"] is not an integer value."));
        }
        return new Person(id, sex, name, age);
    }
}
