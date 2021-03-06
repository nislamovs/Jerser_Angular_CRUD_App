package com.jerseyexample.app.controller;

import com.jerseyexample.app.controller.docs.UserResourceDocs;
import com.jerseyexample.app.converters.UserConverter;
import com.jerseyexample.app.converters.UserDetailsConverter;
import com.jerseyexample.app.domain.requests.CreateUserForm;
import com.jerseyexample.app.domain.requests.UserRequest;
import com.jerseyexample.app.domain.responses.UserCreatedResponse;
import com.jerseyexample.app.model.UserEntity;
import com.jerseyexample.app.services.UserService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Optional;

import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED_TYPE;

@Resource
@Slf4j
@Path("/")
@Api(tags = { "User management" },  description = "Api calls for user operations")
public class UserResource implements UserResourceDocs {

    @Inject
    UserService userService;

    @GET
    @Path("/users/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {

        log.info("Retrieving user list...");
        return Response.status(Response.Status.OK)
                .entity(UserConverter.toResponse(userService.findAllUsers())).build();
    }

    @GET
    @Path("/users/count")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsersCount() {

        HashMap<String, Long> response = new HashMap<>();
        response.put("usersCount", userService.getUsersCount());

        log.info("Retrieving user count...");
        return Response.status(Response.Status.OK).entity(response).build();
    }

    @GET
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPageOfUsers(@DefaultValue("5")   @QueryParam("PageSize") Integer pageSize,
                                   @DefaultValue("1")   @QueryParam("PageNumber") Integer pageNumber,
                                   @DefaultValue("asc") @QueryParam("OrderDirection") String orderDirection,
                                   @DefaultValue("id")  @QueryParam("OrderBy") String orderBy) {

        log.info("Retrieving user page [ pageSize : pageNumber : orderBy : orderDirection == " + pageSize + " : " + pageNumber + " : " + orderBy + " : " + orderDirection + " ]");
        Page<UserEntity> users = userService.findAllUsers(pageSize, pageNumber, orderDirection, orderBy);
        long count = userService.getUsersCount();
        log.info("Users found: " + count + "]");

        return ObjectUtils.isEmpty(users) ? Response.status(Response.Status.NO_CONTENT).build()
                : Response.status(Response.Status.OK).header("user-count", String.valueOf(count))
                .entity(UserConverter.toResponse(users.getContent())).build();
    }

    @GET
    @Path("/users/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserById(@PathParam("id") @Min(value = 0, message = "Request params are invalid.") long id) {

        log.info("Retrieving user by id: [" + id + "]");

        System.out.println(UserDetailsConverter.toResponse(userService.findById(id)));

        return Response.status(Response.Status.OK).entity(UserDetailsConverter.toResponse(userService.findById(id))).build();
    }

    @POST
    @Path("/users")
    @Consumes({MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_FORM_URLENCODED})
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUserByForm ( @BeanParam CreateUserForm userForm) throws IOException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        UserRequest user = UserRequest.builder().firstname(userForm.getFirstname()).lastname(userForm.getLastname())
                .address(userForm.getAddress())
                .email(userForm.getEmail())
                .phone(userForm.getPhone())
                .birthdate(LocalDate.parse(userForm.getBirthdate(), formatter).atStartOfDay())
                .photoImage(IOUtils.toByteArray(userForm.getUserPhotoStream()))
                .description(userForm.getDescription()).skills(userForm.getSkills()).experience(userForm.getExperience())
                .build();

        UserEntity ue = userService.createUser(user);
        UserCreatedResponse response = UserCreatedResponse.builder().firstname(ue.getFirstname()).lastname(ue.getLastname())
                .email(ue.getEmail()).createdDate(LocalDateTime.now()).build();

        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/users")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUserByForm(
            @FormDataParam("id") String id,
            @FormDataParam("firstname") String firstname,
            @FormDataParam("lastname") String lastname,
            @FormDataParam("email") String email,
            @FormDataParam("address") String address,
            @FormDataParam("phone") String phone,
            @FormDataParam("birthdate") String birthdate,

//Find comment in FE project with content 'multipart_base64_content' and uncomment it.
//            @FormDataParam("photo") InputStream base64Stream,

            @FormDataParam("image") FormDataContentDisposition fileDetail,
            @FormDataParam("image") InputStream userPhotoStream,

            @FormDataParam("description") String description,
            @FormDataParam("skills") String skills,
            @FormDataParam("experience") String experience

    ) throws IOException {

        log.info("Updating user by id: [" + id + "]");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        UserRequest user = UserRequest.builder().id(Long.valueOf(id)).firstname(firstname).lastname(lastname)
                .address(address).email(email).phone(phone).birthdate(LocalDate.parse(birthdate, formatter).atStartOfDay())
                .photoImage(IOUtils.toByteArray(userPhotoStream))
                .description(description).skills(skills).experience(experience)
                .build();

        UserEntity ue = userService.createUser(user);
        UserCreatedResponse response = UserCreatedResponse.builder().firstname(ue.getFirstname()).lastname(ue.getLastname())
                .email(ue.getEmail()).createdDate(LocalDateTime.now()).build();

        return Response.status(Response.Status.OK).entity(response).build();
    }

    @DELETE
    @Path("/users/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUserById(@PathParam("id") @Min(value = 1, message = "Request params are invalid.") long id) {

        log.info("Deleting user by id: [" + id + "]");
        userService.deleteUser(id);

        return Response.status(Response.Status.NO_CONTENT).build();
    }

// V2

    @POST
    @Path("/v2/users")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(@Valid UserRequest userRequest) {

        log.info("Creating new user : [" + userRequest.getFirstname() + " " + userRequest.getLastname() + " " + userRequest.getBirthdate() + "]");

        UserEntity ue = userService.createUser(userRequest);
        UserCreatedResponse response = UserCreatedResponse.builder().firstname(ue.getFirstname()).lastname(ue.getLastname())
                .email(ue.getEmail()).createdDate(LocalDateTime.now()).build();

        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/v2/users")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@Valid UserRequest userReq) {

        log.info("Updating user by id: [" + userReq.getId() + "]");
        userService.updateUser(userReq);

        return Response.status(Response.Status.OK).build();
    }
}
