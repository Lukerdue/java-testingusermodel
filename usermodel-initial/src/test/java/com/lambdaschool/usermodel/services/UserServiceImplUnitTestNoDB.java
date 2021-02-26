package com.lambdaschool.usermodel.services;

import com.lambdaschool.usermodel.UserModelApplicationTesting;
import com.lambdaschool.usermodel.exceptions.ResourceNotFoundException;
import com.lambdaschool.usermodel.models.Role;
import com.lambdaschool.usermodel.models.User;
import com.lambdaschool.usermodel.models.UserRoles;
import com.lambdaschool.usermodel.models.Useremail;
import com.lambdaschool.usermodel.repository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserModelApplicationTesting.class, properties={"command.line.runner.enabled = false"})
public class UserServiceImplUnitTestNoDB {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userrepo;

    @MockBean
    private RoleService roleservice;

    @MockBean
    private HelperFunctions helpers;

    private List<User> userList = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        Role r1 = new Role("admin");
        Role r2 = new Role("user");
        Role r3 = new Role("data");

        r1.setRoleid(1);
        r2.setRoleid(2);
        r3.setRoleid(3);

        User u1 = new User("admin",
                "password",
                "admin@lambdaschool.local");
        u1.setUserid(10);
        u1.getRoles()
                .add(new UserRoles(u1,
                        r1));
        u1.getRoles()
                .add(new UserRoles(u1,
                        r2));
        u1.getRoles()
                .add(new UserRoles(u1,
                        r3));

        u1.getUseremails()
                .add(new Useremail(u1,
                        "admin@email.local"));
        u1.getUseremails().get(0).setUseremailid(11);
        u1.getUseremails()
                .add(new Useremail(u1,
                        "admin@mymail.local"));
        u1.getUseremails().get(1).setUseremailid(12);

        userList.add(u1);

        // data, user
        User u2 = new User("cinnamon test",
                "1234567",
                "cinnamon@lambdaschool.local");
        u2.setUserid(20);
        u2.getRoles()
                .add(new UserRoles(u2,
                        r2));
        u2.getRoles()
                .add(new UserRoles(u2,
                        r3));
        u2.getUseremails()
                .add(new Useremail(u2,
                        "cinnamon@mymail.local"));
        u2.getUseremails().get(0).setUseremailid(21);
        u2.getUseremails()
                .add(new Useremail(u2,
                        "hops@mymail.local"));
        u2.getUseremails().get(1).setUseremailid(22);
        u2.getUseremails()
                .add(new Useremail(u2,
                        "bunny@email.local"));
        u2.getUseremails().get(2).setUseremailid(23);
        userList.add(u2);

        // user
        User u3 = new User("barnbarn test",
                "ILuvM4th!",
                "barnbarn@lambdaschool.local");
        u3.setUserid(30);
        u3.getRoles()
                .add(new UserRoles(u3,
                        r2));
        u3.getUseremails()
                .add(new Useremail(u3,
                        "barnbarn@email.local"));
        u3.getUseremails().get(0).setUseremailid(31);
        userList.add(u3);

        User u4 = new User("puttat",
                "password",
                "puttat@school.lambda");
        u4.setUserid(40);
        u4.getRoles()
                .add(new UserRoles(u4,
                        r2));
        userList.add(u4);

        User u5 = new User("misskitty test",
                "password",
                "misskitty@school.lambda");
        u5.setUserid(50);
        u5.getRoles()
                .add(new UserRoles(u5,
                        r2));
        userList.add(u5);

        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void findUserById() {
        Mockito.when(userrepo.findById(10L))
                .thenReturn(Optional.of(userList.get(0)));
        assertEquals("admin", userService.findUserById(10L).getUsername());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void notFindUserById(){
        Mockito.when(userrepo.findById(33L))
                .thenThrow(ResourceNotFoundException.class);

        assertEquals("cinnamon", userService.findUserById(33L).getUsername());
    }

    @Test
    public void findByNameContaining() {
        Mockito.when(userrepo.findByUsernameContainingIgnoreCase("cinn"))
                .thenReturn(userList);

        assertEquals(5, userService.findByNameContaining("cinn").size());
    }

    @Test
    public void findAll() {
        Mockito.when(userrepo.findAll())
                .thenReturn(userList);
        assertEquals(5, userService.findAll().size());
    }

    @Test
    public void delete() {
        Mockito.when(userrepo.findById(1L))
                .thenReturn(Optional.of(userList.get(0)));
        Mockito.doNothing()
                .when(userrepo)
                .deleteById(1L);
        userService.delete(1);
        assertEquals(5, userList.size());
    }

    @Test
    public void findByName() {
        Mockito.when(userrepo.findByUsername("cinnamon test"))
                .thenReturn(userList.get(1));
        assertEquals("cinnamon test", userService.findByName("cinnamon test").getUsername());
    }

    @Test (expected = ResourceNotFoundException.class)
    public void notFindByName(){
        Mockito.when(userrepo.findByUsername("cinn"))
                .thenThrow(ResourceNotFoundException.class);
        assertEquals("test", userrepo.findByUsername("cinn"));
    }

    @Test
    public void save() {
        Role temprole = new Role("admin");
        String username = "test user";
        User tempuser = new User(username, "LambdaLlama", "lamaLove@outlook.com");
        tempuser.getRoles().add(new UserRoles(tempuser, temprole));

        Mockito.when(userrepo.save(any(User.class)))
                .thenReturn(tempuser);
        Mockito.when(helpers.isAuthorizedToMakeChange(tempuser.getUsername()))
                .thenReturn(true);
        Mockito.when(roleservice.findRoleById(0))
                .thenReturn(temprole);
        userService.save(tempuser);
        assertEquals("test user", tempuser.getUsername());
    }

    @Test
    public void update() {
        User upduser = new User("jamie test", "LlamaLambda", "jlh@gmail.com");
        Mockito.when(userrepo.findById(10L))
                .thenReturn(Optional.of(userList.get(1)));
        Mockito.when(helpers.isAuthorizedToMakeChange("cinnamon test"))
                .thenReturn(true);
        Mockito.when(roleservice.findRoleById(1L))
                .thenReturn(new Role("admin"));
        userService.update(upduser, 10);
    }

    @Test
    public void deleteAll() {
        Mockito.doNothing()
                .when(userrepo)
                .deleteAll();
        userService.deleteAll();
        assertEquals(5, userList.size());
    }
}