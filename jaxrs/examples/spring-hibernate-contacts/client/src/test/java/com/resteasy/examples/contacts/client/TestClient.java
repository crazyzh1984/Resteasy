package com.resteasy.examples.contacts.client;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mortbay.jetty.Server;
import org.resteasy.plugins.client.httpclient.ProxyFactory;
import org.resteasy.plugins.providers.RegisterBuiltin;
import org.resteasy.spi.ClientResponse;
import org.resteasy.spi.ResteasyProviderFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.resteasy.examples.contacts.core.Contacts;

@RunWith(SpringJUnit4ClassRunner.class)
// Load the beans to configure, here the embedded jetty
@ContextConfiguration(locations =
{ "/test-config.xml" })
public class TestClient
{
    private static final String USER_EMAIL = "olivier@yahoo.com";

    private ContactClient client;

    // JSR 250 annotation injecting the servletContainer bean. Similar to the
    // Spring @Autowired annotation
    @Resource
    private Server servletContainer;

    @Before
    public void setup()
    {
	try
	{
	    servletContainer.start();

	    while (!servletContainer.isStarted())
	    {
		Thread.sleep(1000);
	    }
	} catch (InterruptedException e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (Exception e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	// this initialization only needs to be done once per VM
	ResteasyProviderFactory.initializeInstance();
	RegisterBuiltin.register(ResteasyProviderFactory.getInstance());

	client = ProxyFactory.create(ContactClient.class,
		"http://localhost:9095/services");
	
/* For manual testing (e.g. browser, infinite loop
	while(true)
	{
	    try
	    {
		while (!servletContainer.isStarted())
		{
		Thread.sleep(3000);
		}
	    } catch (InterruptedException e)
	    {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
	*/
    }

    @After
    public void cleanup()
    {
	System.out.println("IN CLEANUP!!!!!!!!!");
	try
	{
	    servletContainer.stop();
	} catch (Exception e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Test
    public void testGetContacts()
    {
	Assert.assertTrue(servletContainer.isStarted());
	ClientResponse<Contacts> contacts = client.getContacts();
	Assert.assertNotNull(contacts);
	Assert.assertEquals(3, contacts.getEntity().getContacts().size());
	Assert.assertEquals(USER_EMAIL, contacts.getEntity().getContacts().iterator().next().getEmail());
    }
}
