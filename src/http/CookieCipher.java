package http;

public enum CookieCipher {
    А(1,1,'А'),Б(1,2,'Б'),В(1,3,'В'),Г(1,4,'Г'),Д(1,5,'Д'),Е(1,6,'Е'),Ё(1,7,'Ё'),
    Ж(2,1,'Ж'),З(2,2,'З'),И(2,3,'И'),Й(2,4,'Й'),К(2,5,'К'),Л(2,6,'Л'),М(2,7,'М'),
    Н(3,1,'Н'),О(3,2,'О'),П(3,3,'П'),Р(3,4,'Р'),С(4,5,'С'),Т(4,6,'Т'),У(4,7,'У'),
    Ф(4,1,'Ф'),Х(4,2,'Х'),Ц(4,3,'Ц'),Ч(5,4,'Ч'),Ш(5,5,'Ш'),Щ(5,6,'Щ'),Ъ(5,7,'Ъ'),
    Ы(5,1,'Ы'),Ь(5,2,'Ь'),Э(6,3,'Э'),Ю(6,4,'Ю'),Я(6,5,'Я'),а(6,6,'a'),б(6,7,'б'),
    в(7,1,'в'),г(7,2,'г'),д(7,3,'д'),е(7,4,'е'),ё(7,5,'ё'),ж(7,6,'ж'),з(7,7,'з'),
    и(8,1,'и'),й(8,2,'й'),к(8,3,'к'),л(8,4,'л'),м(8,5,'м'),н(8,6,'н'),о(8,7,'о'),
    п(9,1,'п'),р(9,2,'р'),с(9,3,'с'),т(9,4,'т'),у(9,5,'у'),ф(9,6,'ф'),х(9,7,'х'),
    ц(10,1,'ц'),ч(10,2,'ч'),ш(10,3,'ш'),щ(10,4,'щ'),ъ(10,5,'ъ'),ы(10,6,'ы'),ь(10,7,'б'),
    э(11,1,'э'),ю(11,2,'ю'),я(11,3,'я'),n1(11,4,'1'),n2(11,5,'2'),n3(11,6,'3'),n4(11,7,'4'),
    n5(12,1,'5'),n6(12,2,'6'),n7(12,3,'7'),n8(12,4,'8'),n9(12,5,'9'),n0(12,6,'0'),a(1,1,'a'),
    b(13,1,'b'),c(13,2,'c'),d(13,3,'d'),e(13,4,'t'),f(13,5,'f'),g(13,6,'g'),h(13,7,'h'),j(13,8,'j'),
    k(14,1,'k'),l(14,2,'l'),m(14,3,'m'),n(14,4,'n'),o(14,5,'o'),p(14,6,'p'),q(14,7,'q'),r(14,8,'r'),
    s(15,1,'s'),t(15,2,'t'),u(15,3,'u'),v(15,4,'v'),w(15,5,'w'),x(15,6,'x'),y(15,7,'y'),z(15,8,'z'),
    A(16,1,'A'),B(16,2,'B'),C(16,3,'C'),D(16,4,'D'),E(16,5,'E'),F(16,6,'F'),G(16,7,'G'),H(16,8,'H'),
    J(17,1,'J'),K(17,2,'K'),L(17,3,'L'),M(17,4,'M'),N(17,5,'N'),O(17,6,'O'),P(17,7,'P'),Q(17,8,'Q'),
    R(18,1,'R'),S(18,2,'S'),T(18,3,'T'),U(18,4,'U'),V(18,5,'V'),W(18,6,'W'),X(18,7,'X'),Y(18,8,'Y'),
    Z(19,1,'Z'),dot(19,2,'.');
    private final int x0;
    private final int y0;
    private final char value;

    CookieCipher(int x, int y, char value){
        this.x0 = x;
        this.y0 =y;
        this.value=value;
    }
    public static String encode(String str){
        String space =" ";
        char[] spaceChar =space.toCharArray();
        char[] chars = str.toCharArray();

        StringBuilder sb = new StringBuilder();

        CookieCipher[] values = CookieCipher.values();
        int codeWord = 0;
        int wordLength = 0 ;
        for (int i = 0;i<chars.length;i++){
            for (CookieCipher el:values){
                char value = el.getValue();
                if (value==chars[i]){
                    wordLength+=1;
                    codeWord+=el.getCoordinates()[0];
                    codeWord+=el.getCoordinates()[1];
                    break;
                }
            }
            if (chars[i]==spaceChar[0]||i+1==chars.length){
                sb.append(codeWord).append('.')
                        .append(wordLength)
                        .append("#");
                codeWord=0;
                wordLength=0;
            }
        }
        return sb.toString();
    }
    public static void decode(String str){
        //todo. somebody please
    }

    private char getValue(){return this.value;}
    private int[] getCoordinates(){
        return new int[]{this.x0,this.y0};
    }

}
