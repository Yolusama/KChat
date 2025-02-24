package KChat.Functional;

import KChat.Common.Constants;

import java.util.Random;

public class RandomGenerator {
    private RandomGenerator(){}

    private static final String Table = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQUVWXYZ";

    private static final Integer Bound = 10;
    private static final Integer UserIdRandomBound = 5;
    private static final Integer UserAccountRandomBound = 9;
    private static final char UserIdPrefix = 'U';
    private static final char HabitIdPrefix = 'H';
    private static final char VersionIdPrefix = 'V';
    private static final Integer VersionIdLength = 12;
    private static final Integer HabitIdRandomBound = 5;

    public static String generateNumber(int count)
    {
          StringBuilder res = new StringBuilder();
          Random random = new Random(System.currentTimeMillis());
          for(int i=0;i<count;i++)
          {
              res.append(random.nextInt(Bound));
          }
          return res.toString();
    }

    public static String generateWithTable(int count)
    {
        int length = Table.length();
        Random random = new Random(System.currentTimeMillis());
        StringBuilder builder = new StringBuilder();
        for(int i=0;i<count;i++)
            builder.append(random.nextInt(length));
        return builder.toString();
    }

    public static String generateUserId()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(UserIdPrefix);
        Random random = new Random(System.currentTimeMillis());
        int bound = random.nextInt(UserIdRandomBound) + Bound;
        for(int i = 0;i < bound;i++)
            builder.append(random.nextInt(Bound));
        return builder.toString();
    }

    public static String generateUserAccount(){
        StringBuilder builder = new StringBuilder();
        Random random = new Random(System.currentTimeMillis());
        builder.append(random.nextInt(UserAccountRandomBound)+1);
        int count = random.nextInt(2) + Bound;
        for(int i=0;i<count;i++)
            builder.append(random.nextInt(Bound));
        return builder.toString();
    }

    public static String generateHabitId(){
        StringBuilder builder = new StringBuilder();
        builder.append(HabitIdPrefix);
        Random random = new Random(System.currentTimeMillis());
        int bound = random.nextInt(HabitIdRandomBound) + Bound;
        for(int i = 0;i < bound;i++)
            builder.append(random.nextInt(Bound));
        return builder.toString();
    }

    public static String generateVersionId(){
        StringBuilder builder = new StringBuilder();
        builder.append(VersionIdPrefix);
        Random random = new Random(System.currentTimeMillis());
        for(int i=2;i<VersionIdLength;i++)
        {
            int rIndex = random.nextInt(Table.length());
            char c = Table.charAt(rIndex);
            if(c==VersionIdPrefix||c=='v')
            {
                int w = random.nextInt(2);
                if(w== Constants.AbnormalState)
                    c -= random.nextInt(Bound-1)+1;
                else
                    c += random.nextInt(Bound-1)+1;
            }
            builder.append(c);
        }
        return builder.toString();
    }
}
