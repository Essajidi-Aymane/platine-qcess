package univ.lille.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import univ.lille.application.service.FileStorageService;
import univ.lille.domain.port.in.UserPort;
import univ.lille.dto.auth.user.CreateUserRequest;
import univ.lille.dto.auth.user.UpdateProfileRequest;
import univ.lille.dto.auth.user.UserDTO;
import univ.lille.dto.role.AssignRolesToUserRequest;
import univ.lille.dto.role.UnassignCustomRoleRequest;
import univ.lille.dto.users.UserResponse;
import univ.lille.infrastructure.adapter.security.QcessUserPrincipal;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerUnitTest {

    @Mock
    private UserPort userPort;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private UserController userController;

    @Test
    void createUser_returnsCreatedUserAndStatus201() {
        // GIVEN
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("user@test.fr");
        request.setFirstName("Aymane");
        request.setLastName("Essajidi");

        UserDTO returnedUser = new UserDTO();
        returnedUser.setId(1L);
        returnedUser.setEmail("user@test.fr");
        returnedUser.setFirstName("Aymane");
        returnedUser.setLastName("Essajidi");
        returnedUser.setCreatedAt(LocalDateTime.now());

        when(userPort.createUser(request)).thenReturn(returnedUser);

        // WHEN
        var response = userController.createUser(request);

        // THEN
        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("user@test.fr", response.getBody().getEmail());
        assertEquals(1L, response.getBody().getId());
        verify(userPort).createUser(request);
    }

    @Test
    void getUsersOfOrganization_returnsUsersAndMetaForAdmin() {
        // GIVEN
        Long orgId = 2L;

        // mock du principal
        QcessUserPrincipal principal = mock(QcessUserPrincipal.class);
        when(principal.getOrganizationId()).thenReturn(orgId);

        // mock des users retournés par le use case
        UserDTO user1 = new UserDTO();
        user1.setId(3L);
        user1.setEmail("aymaneessajidi@gmail.com");
        user1.setFirstName("Aymane");
        user1.setLastName("Essajidi");
        user1.setOrganisationId(orgId);

        UserDTO user2 = new UserDTO();
        user2.setId(2L);
        user2.setEmail("admin@test.fr");
        user2.setFullName("Admin Test");
        user2.setOrganisationId(orgId);

        when(userPort.getUsersByOrganizationId(orgId))
                .thenReturn(List.of(user1, user2));

        // WHEN
        var response = userController.getUsersOfOrganization(principal);

        // THEN
        assertEquals(200, response.getStatusCode().value());
        UserResponse body = response.getBody();
        assertNotNull(body);

        // Vérifier la liste
        List<UserDTO> data = body.getData();
        assertNotNull(data);
        assertEquals(2, data.size());
        assertEquals("aymaneessajidi@gmail.com", data.get(0).getEmail());
        assertEquals("admin@test.fr", data.get(1).getEmail());

        // Vérifier le meta
        Map<String, Object> meta = body.getMeta();
        assertNotNull(meta);
        assertEquals(2, meta.get("size"));
        assertEquals(orgId, meta.get("organizationId"));

        // Vérifier que le use case a bien été appelé
        verify(userPort).getUsersByOrganizationId(orgId);
    }

    @Test
    void suspendUser_should_call_port_and_return_success_message() {
        // GIVEN
        Long userId = 10L;
        Long orgId = 5L;

        QcessUserPrincipal principal = mock(QcessUserPrincipal.class);
        when(principal.getOrganizationId()).thenReturn(orgId);

        doNothing().when(userPort).suspendUser(userId, orgId);

        // WHEN
        ResponseEntity<String> response = userController.suspendUser(userId, principal);

        // THEN
        assertEquals(200, response.getStatusCode().value());
        assertEquals("User suspended successfully", response.getBody());
        verify(userPort).suspendUser(userId, orgId);
    }

    @Test
    void activateUser_should_call_port_and_return_success_message() {
        // GIVEN
        Long userId = 12L;
        Long orgId = 3L;

        QcessUserPrincipal principal = mock(QcessUserPrincipal.class);
        when(principal.getOrganizationId()).thenReturn(orgId);

        doNothing().when(userPort).activateUser(userId, orgId);

        // WHEN
        ResponseEntity<String> response = userController.activateUser(userId, principal);

        // THEN
        assertEquals(200, response.getStatusCode().value());
        assertEquals("User activated successfully", response.getBody());
        verify(userPort).activateUser(userId, orgId);
    }

    @Test
    void assignRoleToUsers_should_call_port_and_return_204() {
        // GIVEN
        Long roleId = 7L;
        List<Long> userIds = List.of(1L, 2L, 3L);
        Long orgId = 4L;

        AssignRolesToUserRequest request = new AssignRolesToUserRequest();
        request.setRoleId(roleId);
        request.setUserIds(userIds);

        QcessUserPrincipal principal = mock(QcessUserPrincipal.class);
        when(principal.getOrganizationId()).thenReturn(orgId);

        doNothing().when(userPort).assignCustomRoleToUsers(roleId, userIds, orgId);

        // WHEN
        ResponseEntity<Void> response = userController.assignRoleToUsers(request, principal);

        // THEN
        assertEquals(204, response.getStatusCode().value());
        assertNull(response.getBody());
        verify(userPort).assignCustomRoleToUsers(roleId, userIds, orgId);
    }

    @Test
    void unassignCustomRole_should_call_port_and_return_200() {
        // GIVEN
        Long roleId = 9L;
        List<Long> userIds = List.of(5L, 6L);
        Long orgId = 2L;
        Long adminId = 100L;

        UnassignCustomRoleRequest request = new UnassignCustomRoleRequest();
        request.setRoleId(roleId);
        request.setUserIds(userIds);

        QcessUserPrincipal principal = mock(QcessUserPrincipal.class);
        when(principal.getId()).thenReturn(adminId);
        when(principal.getOrganizationId()).thenReturn(orgId);

        doNothing().when(userPort).unassignCustomRoleFromUsers(roleId, userIds, orgId, adminId);

        // WHEN
        ResponseEntity<Void> response = userController.unassignCustomRole(request, principal);

        // THEN
        assertEquals(200, response.getStatusCode().value());
        verify(userPort).unassignCustomRoleFromUsers(roleId, userIds, orgId, adminId);
    }


    @Test
    void getCurrentUser_should_return_current_user_profile() {
        // GIVEN
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("user@test.fr");
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");

        when(userPort.getCurrentUserProfile()).thenReturn(userDTO);

        // WHEN
        ResponseEntity<UserDTO> response = userController.getCurrentUser();

        // THEN
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("user@test.fr", response.getBody().getEmail());
        assertEquals("John", response.getBody().getFirstName());
        verify(userPort).getCurrentUserProfile();
    }

    @Test
    void updateCurrentUser_should_update_and_return_updated_profile() {
        // GIVEN
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setEmail("jane.smith@test.fr");

        UserDTO updatedUser = new UserDTO();
        updatedUser.setId(1L);
        updatedUser.setEmail("jane.smith@test.fr");
        updatedUser.setFirstName("Jane");
        updatedUser.setLastName("Smith");

        when(userPort.updateProfile(request)).thenReturn(updatedUser);

        // WHEN
        ResponseEntity<UserDTO> response = userController.updateCurrentUser(request);

        // THEN
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("jane.smith@test.fr", response.getBody().getEmail());
        assertEquals("Jane", response.getBody().getFirstName());
        assertEquals("Smith", response.getBody().getLastName());
        verify(userPort).updateProfile(request);
    }

    @Test
    void updateProfilePicture_should_return_badRequest_when_file_is_null() {
        // WHEN
        ResponseEntity<UserDTO> response = userController.updateProfilePicture(null);

        // THEN
        assertEquals(400, response.getStatusCode().value());
        verifyNoInteractions(fileStorageService);
        verify(userPort, never()).updateProfilePicture(any());
    }

    @Test
    void updateProfilePicture_should_return_badRequest_when_file_is_empty() {
        // GIVEN
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file", "avatar.png", "image/png", new byte[0]
        );

        // WHEN
        ResponseEntity<UserDTO> response = userController.updateProfilePicture(emptyFile);

        // THEN
        assertEquals(400, response.getStatusCode().value());
        verifyNoInteractions(fileStorageService);
        verify(userPort, never()).updateProfilePicture(any());
    }

    @Test
    void updateProfilePicture_should_return_payloadTooLarge_when_file_exceeds_limit() {
        // GIVEN
        byte[] largeContent = new byte[11 * 1024 * 1024]; // 11 MB > 10 MB limit
        MockMultipartFile largeFile = new MockMultipartFile(
                "file", "avatar.png", "image/png", largeContent
        );

        // WHEN
        ResponseEntity<UserDTO> response = userController.updateProfilePicture(largeFile);

        // THEN
        assertEquals(413, response.getStatusCode().value());
        verifyNoInteractions(fileStorageService);
        verify(userPort, never()).updateProfilePicture(any());
    }

    @Test
    void updateProfilePicture_should_upload_and_return_updated_user() throws Exception {
        // GIVEN
        Long userId = 1L;
        byte[] content = "fake image content".getBytes();
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.png", "image/png", content
        );
        String publicUrl = "https://storage.example.com/avatars/1/avatar.png";

        UserDTO currentUser = new UserDTO();
        currentUser.setId(userId);

        UserDTO updatedUser = new UserDTO();
        updatedUser.setId(userId);
        updatedUser.setProfilePictureUrl(publicUrl);

        when(userPort.getCurrentUserProfile()).thenReturn(currentUser);
        when(fileStorageService.saveUserAvatar(userId, file)).thenReturn(publicUrl);
        when(userPort.updateProfilePicture(publicUrl)).thenReturn(updatedUser);

        // WHEN
        ResponseEntity<UserDTO> response = userController.updateProfilePicture(file);

        // THEN
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(publicUrl, response.getBody().getProfilePictureUrl());
        verify(userPort).getCurrentUserProfile();
        verify(fileStorageService).saveUserAvatar(userId, file);
        verify(userPort).updateProfilePicture(publicUrl);
    }

    @Test
    void updateProfilePicture_should_return_internalServerError_on_exception() throws Exception {
        // GIVEN
        Long userId = 1L;
        byte[] content = "fake image content".getBytes();
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.png", "image/png", content
        );

        UserDTO currentUser = new UserDTO();
        currentUser.setId(userId);

        when(userPort.getCurrentUserProfile()).thenReturn(currentUser);
        when(fileStorageService.saveUserAvatar(userId, file)).thenThrow(new RuntimeException("Storage error"));

        // WHEN
        ResponseEntity<UserDTO> response = userController.updateProfilePicture(file);

        // THEN
        assertEquals(500, response.getStatusCode().value());
        verify(userPort).getCurrentUserProfile();
        verify(fileStorageService).saveUserAvatar(userId, file);
        verify(userPort, never()).updateProfilePicture(any());
    }
}
