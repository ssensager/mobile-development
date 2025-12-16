package com.example.ai_for_bgu;

public class BguCalc {
    public static double calculateCalories(
            boolean isMale,
            int age,
            int height,
            double weight,
            String goal
    ) {

        double bmr;

        if (isMale) {
            bmr = 10 * weight + 6.25 * height - 5 * age + 5;
        } else {
            bmr = 10 * weight + 6.25 * height - 5 * age - 161;
        }

        if (goal.equals("Похудение")) {
            bmr = bmr * 0.85;
        } else if (goal.equals("Набор массы")) {
            bmr = bmr * 1.15;
        }

        return bmr;
    }

    public static String calculateBJU(double calories, double weight) {

        double proteins = weight * 2;
        double fats = weight * 1;

        double proteinsCalories = proteins * 4;
        double fatsCalories = fats * 9;

        double carbsCalories = calories - (proteinsCalories + fatsCalories);
        double carbs = carbsCalories / 4;

        return "Калории: " + Math.round(calories) + " ккал\n" +
                "Белки: " + Math.round(proteins) + " г\n" +
                "Жиры: " + Math.round(fats) + " г\n" +
                "Углеводы: " + Math.round(carbs) + " г";
    }
}
