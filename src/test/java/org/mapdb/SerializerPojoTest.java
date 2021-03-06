package org.mapdb;


import junit.framework.TestCase;

import javax.swing.*;
import java.io.File;
import java.io.IOError;
import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class SerializerPojoTest extends TestCase {

    SerializerPojo p = new SerializerPojo(new CopyOnWriteArrayList<SerializerPojo.ClassInfo>());

    enum Order
    {
        ASCENDING,
        DESCENDING
    }

    public void testEnum() throws Exception{
        Order o = Order.ASCENDING;
        o = (Order) Utils.clone(o, p);
        assertEquals(o,Order.ASCENDING );
        assertEquals(o.ordinal(),Order.ASCENDING .ordinal());
        assertEquals(o.name(),Order.ASCENDING .name());

        o = Order.DESCENDING;
        o = (Order) Utils.clone(o, p);
        assertEquals(o,Order.DESCENDING );
        assertEquals(o.ordinal(),Order.DESCENDING .ordinal());
        assertEquals(o.name(),Order.DESCENDING .name());

    }

// TODO Externalizable support
//    static class Extr implements  Externalizable{
//
//        int aaa = 11;
//        String  l = "agfa";
//
//        public void writeExternal(ObjectOutput out) throws IOException {
//            out.writeObject(l);
//            out.writeInt(aaa);
//
//        }
//
//        public void readExternal(ObjectInput in) throws IOException{
//            l = (String) in.readObject();
//            aaa = in.readInt()+1;
//
//        }
//    }
//
//    @Test public void testExternalizable() throws Exception{
//        Extr e = new Extr();
//        e.aaa = 15;
//        e.l = "pakla";
//
//        e = (Extr) deserialize(serialize(e));
//        assertEquals(e.aaa,16); //was incremented during serialization
//        assertEquals(e.l,"pakla");
//
//    }


    static class Bean1 implements Serializable {

    	private static final long serialVersionUID = -2549023895082866523L;

		@Override
		public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Bean1 bean1 = (Bean1) o;

            if (Double.compare(bean1.doubleField, doubleField) != 0) return false;
            if (Float.compare(bean1.floatField, floatField) != 0) return false;
            if (intField != bean1.intField) return false;
            if (longField != bean1.longField) return false;
            if (field1 != null ? !field1.equals(bean1.field1) : bean1.field1 != null) return false;
            if (field2 != null ? !field2.equals(bean1.field2) : bean1.field2 != null) return false;

            return true;
        }


        protected String field1 = null;
        protected String field2 = null;

        protected int intField = Integer.MAX_VALUE;
        protected long longField = Long.MAX_VALUE;
        protected double doubleField = Double.MAX_VALUE;
        protected float floatField = Float.MAX_VALUE;

        transient int getCalled = 0;
        transient int setCalled = 0;

        public String getField2() {
            getCalled++;
            return field2;
        }

        public void setField2(String field2) {
            setCalled++;
            this.field2 = field2;
        }

        Bean1(String field1, String field2) {
            this.field1 = field1;
            this.field2 = field2;
        }

        Bean1() {
        }
    }

    static class Bean2 extends Bean1 {

		private static final long serialVersionUID = 8376654194053933530L;

		@Override
		public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            Bean2 bean2 = (Bean2) o;

            if (field3 != null ? !field3.equals(bean2.field3) : bean2.field3 != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return field3 != null ? field3.hashCode() : 0;
        }

        private String field3 = null;

        Bean2(String field1, String field2, String field3) {
            super(field1, field2);
            this.field3 = field3;
        }

        Bean2() {
        }
    }



    Bean1 b = new Bean1("aa", "bb");
    Bean2 b2 = new Bean2("aa", "bb", "cc");

    public void testGetFieldValue1() throws Exception {
        assertEquals("aa", p.getFieldValue("field1", b));
    }

    public void testGetFieldValue2() throws Exception {
        assertEquals("bb", p.getFieldValue("field2", b));
        assertEquals(1, b.getCalled);
    }

    public void testGetFieldValue3() throws Exception {
        assertEquals("aa", p.getFieldValue("field1", b2));
    }

    public void testGetFieldValue4() throws Exception {
        assertEquals("bb", p.getFieldValue("field2", b2));
        assertEquals(1, b2.getCalled);
    }

    public void testGetFieldValue5() throws Exception {
        assertEquals("cc", p.getFieldValue("field3", b2));
    }

    public void testSetFieldValue1() {
        p.setFieldValue("field1", b, "zz");
        assertEquals("zz", b.field1);
    }

    public void testSetFieldValue2() {
        p.setFieldValue("field2", b, "zz");
        assertEquals("zz", b.field2);
        assertEquals(1, b.setCalled);
    }

    public void testSetFieldValue3() {
        p.setFieldValue("field1", b2, "zz");
        assertEquals("zz", b2.field1);
    }

    public void testSetFieldValue4() {
        p.setFieldValue("field2", b2, "zz");
        assertEquals("zz", b2.field2);
        assertEquals(1, b2.setCalled);
    }

    public void testSetFieldValue5() {
        p.setFieldValue("field3", b2, "zz");
        assertEquals("zz", b2.field3);
    }

    public void testGetPrimitiveField() {
        assertEquals(Integer.MAX_VALUE, p.getFieldValue("intField", b2));
        assertEquals(Long.MAX_VALUE, p.getFieldValue("longField", b2));
        assertEquals(Double.MAX_VALUE, p.getFieldValue("doubleField", b2));
        assertEquals(Float.MAX_VALUE, p.getFieldValue("floatField", b2));
    }


    public void testSetPrimitiveField() {
        p.setFieldValue("intField", b2, -1);
        assertEquals(-1, p.getFieldValue("intField", b2));
        p.setFieldValue("longField", b2, -1L);
        assertEquals(-1L, p.getFieldValue("longField", b2));
        p.setFieldValue("doubleField", b2, -1D);
        assertEquals(-1D, p.getFieldValue("doubleField", b2));
        p.setFieldValue("floatField", b2, -1F);
        assertEquals(-1F, p.getFieldValue("floatField", b2));
    }



    public void testSerializable() throws Exception {

        assertEquals(b, Utils.clone(b, p));
    }


    public void testRecursion() throws Exception {
        AbstractMap.SimpleEntry b = new AbstractMap.SimpleEntry("abcd", null);
        b.setValue(b.getKey());

        AbstractMap.SimpleEntry bx = (AbstractMap.SimpleEntry) Utils.clone(b, p);
        assertEquals(bx, b);
        assert (bx.getKey() == bx.getValue());

    }

    public void testRecursion2() throws Exception {
        AbstractMap.SimpleEntry b = new AbstractMap.SimpleEntry("abcd", null);
        b.setValue(b);

        AbstractMap.SimpleEntry bx = (AbstractMap.SimpleEntry) Utils.clone(b, p);
        assertTrue(bx == bx.getValue());
        assertEquals(bx.getKey(), "abcd");

    }


    public void testRecursion3() throws Exception {
        ArrayList l = new ArrayList();
        l.add("123");
        l.add(l);

        ArrayList l2 = (ArrayList) Utils.clone(l, p);

        assertTrue(l2.size() == 2);
        assertEquals(l2.get(0), "123");
        assertTrue(l2.get(1) == l2);
    }

    public void testPersistedSimple() throws Exception {

        File f = Utils.tempDbFile();
        DB r1 = DBMaker.newFileDB(f).asyncWriteDisable().make();
        long recid = r1.engine.put("AA",r1.getDefaultSerializer());
        r1.commit();
        r1.close();

         r1 = DBMaker.newFileDB(f).asyncWriteDisable().make();

        String a2 = (String) r1.engine.get(recid, r1.getDefaultSerializer());
        r1.close();
        assertEquals("AA", a2);

    }


    public void testPersisted() throws Exception {
        Bean1 b1 = new Bean1("abc", "dcd");
        File f = Utils.tempDbFile();
        DB r1 = DBMaker.newFileDB(f).asyncWriteDisable().make();
        long recid = r1.engine.put(b1, r1.getDefaultSerializer());
        r1.commit();
        r1.close();

        r1 = DBMaker.newFileDB(f).asyncWriteDisable().make();

        Bean1 b2 = (Bean1) r1.engine.get(recid,r1.getDefaultSerializer());
        r1.close();
        assertEquals(b1, b2);

    }


    public void test_write_object_assertion(){
        try{
            DB db = DBMaker.newMemoryDB().asyncWriteDisable().make();
            db.engine.put(new GregorianCalendar(1,1,1), db.getDefaultSerializer());
            fail();
        }catch(IOError e){
            assertTrue(e.getCause().getMessage().contains("#17"));
        }

        try{
            DB db = DBMaker.newMemoryDB().asyncWriteDisable().make();
            db.engine.put(new JLabel("aa"), db.getDefaultSerializer());
            fail();
        }catch(IOError e){
            assertTrue(e.getCause().getMessage().contains("#17"));
        }

    }


}
