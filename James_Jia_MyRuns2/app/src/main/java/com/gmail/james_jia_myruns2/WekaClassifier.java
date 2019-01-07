package com.gmail.james_jia_myruns2;

// Generated with Weka 3.8.1

class WekaClassifier {

    public static double classify(Object[] i)
            throws Exception {

        double p = Double.NaN;
        p = WekaClassifier.N3065e1d30(i);
        return p;
    }
    static double N3065e1d30(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 76.523851) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() > 76.523851) {
            p = WekaClassifier.N30b5cc1b1(i);
        }
        return p;
    }
    static double N30b5cc1b1(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() <= 451.28639) {
            p = WekaClassifier.N70e915a92(i);
        } else if (((Double) i[0]).doubleValue() > 451.28639) {
            p = 2;
        }
        return p;
    }
    static double N70e915a92(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 1;
        } else if (((Double) i[4]).doubleValue() <= 22.41529) {
            p = 1;
        } else if (((Double) i[4]).doubleValue() > 22.41529) {
            p = WekaClassifier.N7bd892443(i);
        }
        return p;
    }
    static double N7bd892443(Object []i) {
        double p = Double.NaN;
        if (i[8] == null) {
            p = 1;
        } else if (((Double) i[8]).doubleValue() <= 10.807448) {
            p = WekaClassifier.N64ac9f954(i);
        } else if (((Double) i[8]).doubleValue() > 10.807448) {
            p = WekaClassifier.N429b44b76(i);
        }
        return p;
    }
    static double N64ac9f954(Object []i) {
        double p = Double.NaN;
        if (i[15] == null) {
            p = 1;
        } else if (((Double) i[15]).doubleValue() <= 2.065172) {
            p = WekaClassifier.N7a67420f5(i);
        } else if (((Double) i[15]).doubleValue() > 2.065172) {
            p = 1;
        }
        return p;
    }
    static double N7a67420f5(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() <= 312.710187) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() > 312.710187) {
            p = 2;
        }
        return p;
    }
    static double N429b44b76(Object []i) {
        double p = Double.NaN;
        if (i[26] == null) {
            p = 2;
        } else if (((Double) i[26]).doubleValue() <= 3.953054) {
            p = 2;
        } else if (((Double) i[26]).doubleValue() > 3.953054) {
            p = 1;
        }
        return p;
    }
}

