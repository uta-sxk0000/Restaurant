
package com.example.queue; //for android studio

import java.util.ArrayList;

public class admin_queue
{
    private static ArrayList<Customer> queue = new ArrayList<>();

    /public static void addCustomerAutomatically(String customerInfo)
    {
        queue.add(new Customer(customerInfo));
        System.out.println("Customer added: " + customerInfo);
    } connect to customer info/

    public static void showQueue()
    {
        for (int i = 0; i < queue.size(); i++)
        {
            System.out.println("Customer " + (i + 1) + /"Info: " + queue.get(i).getInfo()
                    +/ "Wait: " + queue.get(i).getMinutesInQueue());
        }
    }

    static class Customer
    { //add party info later for now is time and shit
        private long joinTime;
        private String info;

        public Customer(String info)
        {
            this.info = info;
            this.joinTime = System.currentTimeMillis();
        }

        public String getInfo()
        {
            return info;
        }

        public long getMinutesInQueue()
        {
            return (System.currentTimeMillis() - joinTime) / 60000; // convert ms to minutes
        }
    }
}