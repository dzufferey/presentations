class Set {
    protected int sver, size;
    private Element head;
    public Set() {
        sver := size := 0;
        head := null;
    }
    public void add(int elem) {
        bool duplicate := false;
        Element x := head;
        while (x != null) {
            if (x.val == elem) {
                duplicate := true;
                break;
            }
            x := x.next;
        }
        if (!duplicate) {
            temp := new Element(elem,head);
            head := temp;
            sver++; size++;
        }
    }
    protected void delete(int pos) {
        Element x := head, y := head;
        int i := 1;
        while (i < pos) {
            y := x; x := x.next;
            i++;
        }
        if (pos == 1)
            head := x.next;
            else
                y.next := x.next;
                sver++; size--;
    }
    protected int get(int pos) {
        Element x := head;
        int i := 1;
        while (i < pos) {
            x := x.next;
            i++;
        }
        return(x.val);
    }
    public Iterator iterator() {
        return (new Iterator(this));
    }
}

class Iterator {
    int iver, pos;
    Set it_of;
    protected Iterator(Class S) {
        it_of := S;
        pos := 0;
        iver := S.sver;
    }
    public boolean has_next() {
        return(pos < it_of.size);
    }
    public int next() {
        if (iver == S.sver) then
            return(it_of.get(++pos));
            else
                throw new ConcurModException();
    }
    public void remove() {
        if (iver == S.sver) then {
            it_of.delete(pos);
            iver := S.sver;
        }
        else
            throw new ConcurModException();
    }
}

