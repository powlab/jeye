package org.powlab.jeye.scenario.data;

import java.io.IOException;
import java.io.InputStream;

public class Sample4 {

    public boolean try0(Integer value, int k) {
        try {
            k++;
        } catch (Throwable e) {
            value = null;
        } finally {
            k--;
        }
        return false;
    }

    public boolean try1(Integer value, int k) {
        try {
            try {
                k++;
            } finally {
                k--;
            }
        } catch (Throwable e) {
            value = null;
        } finally {
            k--;
        }
        return false;
    }

    public boolean try2(Integer value, int k) {
        try {
            k++;
        } finally {
            value = null;
            System.out.println(++k);
        }
        return false;
    }

    public boolean try9(Integer value, int k) {
        try {
            k++;
            k++;
            k++;
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }

    public boolean try3(Integer value, int k) {
        try {
            k++;
            try {
                return new Boolean(value.toString());
            } catch (Exception ex) {
                throw ex;
            }
        } catch (Exception e) {
            value = null;
        }
        return false;
    }

    public boolean try4(Integer value, int k) {
        try {
            k++;
        } catch (Exception e) {
            k--;
            value = null;
        } catch (Throwable e) {
            value = null;
        }
        return false;
    }

    public boolean try5(Integer value, int k) {
        try {
            k++;
        } catch (IllegalStateException | IndexOutOfBoundsException iofbe) {
            value = null;
        }
        return false;
    }

    public boolean try6(InputStream value, int k) throws IOException {
        try (InputStream source = value) {
            return source.markSupported();
        }
    }

    public boolean try7(Integer value, int k) {
        try {
            try {
                k++;
            } finally {
                k--;
            }
        } finally {
            k--;
        }
        return false;
    }

    public boolean try8(Integer value, int k) {
        try {
            try {
                k += 1;
            } catch (RuntimeException e) {
                k += 2;
            } catch (Exception e) {
                k += 3;
            } finally {
                k += 4;
            }
        } catch (IllegalStateException e) {
            k += 5;
        } finally {
            k += 6;
        }
        return false;
    }

    public void tryq(Integer value, int k) {
        try {
            try8(1, 1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            k++;
        }
    }

    public void tryItOut() {
    }

    public void wrapItUp() {
    }

    public void tryw() {
        try {
            tryItOut();
        } finally {
            wrapItUp();
        }
    }

    public int trye(Integer value, int k) {
        try {
            value = null;
        } catch (Exception e) {
            k--;
            k++;
            throw new RuntimeException(e);
        } finally {
            ++k;
        }
        return k;
    }

    public int tryr(int k) {
        try {
            k += 2;
        } catch (Exception ex) {
            k -= 2;
            throw new RuntimeException(ex);
        } finally {
            k++;
            k++;
            k++;
            k++;
            k++;
            k++;
            k++;
            k++;
            k++;
            k++;
            k++;
            k++;
            k++;
            k++;
            k++;
            k++;
            k++;
            k++;
        }
        return k;
    }

    public int tryt(int k) {
        try {
            k++;
        } catch (Exception ex) {
            k--;
            throw ex;
        } finally {
            k++;
        }
        return k;
    }

    public int tryy(int k) {
        try {
            k++;
            return k;
        } catch (Exception ex) {
            k--;
            return k;
        } finally {
            k++;
            if (k == 2) {
                System.out.println(k);
            } else {
                System.out.println(--k);
            }
        }
    }

    int m = 0;

    public int tryu(int k) {
        try {
            k++;
            return k;
        } catch (Exception ex) {
            m -= 2;
            throw ex;
        } finally {
            m++;
        }
    }

    public void tryi(int k) {
        try {
            k++;
        } catch (Exception ex) {
            m -= 2;
            throw ex;
        }
    }

    public int tryo(int k) {
        try {
            k++;
        } catch (Exception ex) {
            throw ex;
        } finally {
            k--;
            return k;
        }
    }

}
