/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jasontiemann
 */
public class NodeTest {
    @Test
    public void testAddAndActivate() {
        System.out.println("add");
        float input = 0.0f;
        Node instance = new Node();
        instance.add(input);
        assertNotNull("Node returned nothing", instance.activate());
    }

}
