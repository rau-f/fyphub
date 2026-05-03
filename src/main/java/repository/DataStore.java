package repository;

import model.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class DataStore {

    // ---- Called once at startup (now delegates to DatabaseManager) ----
    public static void initialize() {
        DatabaseManager.initialize();
    }

    // ==================== USER CRUD ====================

    public static User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE LOWER(email) = LOWER(?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapUser(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public static User getUserByID(String userID) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, userID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapUser(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public static Map<String, User> getUsers() {
        Map<String, User> map = new LinkedHashMap<>();
        try (Statement s = conn().createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM users")) {
            while (rs.next()) { User u = mapUser(rs); map.put(u.getUserID(), u); }
        } catch (SQLException e) { e.printStackTrace(); }
        return map;
    }

    public static List<Supervisor> getAllSupervisors() {
        List<Supervisor> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'SUPERVISOR'";
        try (Statement s = conn().createStatement(); ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) list.add((Supervisor) mapUser(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static List<Evaluator> getAllEvaluators() {
        List<Evaluator> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'EVALUATOR'";
        try (Statement s = conn().createStatement(); ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) list.add((Evaluator) mapUser(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static void saveUser(User user) {
        String sql = "INSERT INTO users VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, user.getUserID());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole().name());
            ps.setString(5, user.getFullName());
            if (user instanceof Student st) {
                ps.setString(6, st.getStudentID()); ps.setString(7, st.getDepartment());
                ps.setFloat(8, st.getCgpa()); ps.setString(9, st.getProjectStatus());
                ps.setNull(10, Types.VARCHAR); ps.setNull(11, Types.VARCHAR);
                ps.setNull(12, Types.VARCHAR); ps.setNull(13, Types.VARCHAR); ps.setNull(14, Types.VARCHAR);
            } else if (user instanceof Supervisor sup) {
                ps.setNull(6, Types.VARCHAR); ps.setString(7, sup.getDepartment());
                ps.setNull(8, Types.REAL); ps.setNull(9, Types.VARCHAR);
                ps.setString(10, sup.getFacultyID()); ps.setString(11, sup.getSpecialization());
                ps.setNull(12, Types.VARCHAR); ps.setNull(13, Types.VARCHAR); ps.setNull(14, Types.VARCHAR);
            } else if (user instanceof Coordinator coord) {
                ps.setNull(6, Types.VARCHAR); ps.setString(7, coord.getDepartment());
                ps.setNull(8, Types.REAL); ps.setNull(9, Types.VARCHAR);
                ps.setNull(10, Types.VARCHAR); ps.setNull(11, Types.VARCHAR);
                ps.setString(12, coord.getCoordinatorID()); ps.setNull(13, Types.VARCHAR); ps.setNull(14, Types.VARCHAR);
            } else if (user instanceof Evaluator ev) {
                ps.setNull(6, Types.VARCHAR); ps.setNull(7, Types.VARCHAR);
                ps.setNull(8, Types.REAL); ps.setNull(9, Types.VARCHAR);
                ps.setNull(10, Types.VARCHAR); ps.setNull(11, Types.VARCHAR);
                ps.setNull(12, Types.VARCHAR); ps.setString(13, ev.getEvaluatorID()); ps.setString(14, ev.getOrganization());
            } else {
                for (int i = 6; i <= 14; i++) ps.setNull(i, Types.VARCHAR);
            }
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ==================== PROPOSAL CRUD ====================

    public static Map<String, Proposal> getProposals() {
        Map<String, Proposal> map = new LinkedHashMap<>();
        try (Statement s = conn().createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM proposals")) {
            while (rs.next()) { Proposal p = mapProposal(rs); map.put(p.getProposalID(), p); }
        } catch (SQLException e) { e.printStackTrace(); }
        return map;
    }

    public static Proposal getProposalByID(String id) {
        String sql = "SELECT * FROM proposals WHERE proposal_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapProposal(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public static List<Proposal> getProposalsByStudentID(String studentUserID) {
        List<Proposal> list = new ArrayList<>();
        String sql = "SELECT * FROM proposals WHERE student_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, studentUserID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapProposal(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static void saveProposal(Proposal p) {
        String sql = "INSERT INTO proposals VALUES(?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, p.getProposalID());
            ps.setString(2, p.getTitle());
            ps.setString(3, p.getDescription());
            ps.setString(4, p.getStatus().name());
            ps.setString(5, p.getSubmittedAt().toString());
            ps.setFloat(6, p.getPlagiarismScore());
            ps.setString(7, p.getStudentID());
            ps.setString(8, p.getSupervisorID());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void updateProposal(Proposal p) {
        String sql = "UPDATE proposals SET title=?, description=?, status=?, plagiarism_score=?, supervisor_id=? WHERE proposal_id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, p.getTitle());
            ps.setString(2, p.getDescription());
            ps.setString(3, p.getStatus().name());
            ps.setFloat(4, p.getPlagiarismScore());
            ps.setString(5, p.getSupervisorID());
            ps.setString(6, p.getProposalID());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ==================== PROJECT CRUD ====================

    public static Map<String, Project> getProjects() {
        Map<String, Project> map = new LinkedHashMap<>();
        try (Statement s = conn().createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM projects")) {
            while (rs.next()) { Project p = mapProject(rs); map.put(p.getProjectID(), p); }
        } catch (SQLException e) { e.printStackTrace(); }
        return map;
    }

    public static Project getProjectByID(String id) {
        String sql = "SELECT * FROM projects WHERE project_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapProject(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public static Project getProjectByProposalID(String proposalID) {
        String sql = "SELECT * FROM projects WHERE proposal_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, proposalID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapProject(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public static void saveProject(Project p) {
        String sql = "INSERT INTO projects VALUES(?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, p.getProjectID());
            ps.setString(2, p.getTitle());
            ps.setString(3, p.getAbstractText());
            ps.setString(4, p.getStatus().name());
            ps.setString(5, p.getSubmissionDate().toString());
            ps.setString(6, p.getProposalID());
            ps.setString(7, p.getSupervisorID());
            ps.setString(8, p.getCoordinatorID());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void updateProject(Project p) {
        String sql = "UPDATE projects SET title=?, abstract_text=?, status=?, supervisor_id=?, coordinator_id=? WHERE project_id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, p.getTitle());
            ps.setString(2, p.getAbstractText());
            ps.setString(3, p.getStatus().name());
            ps.setString(4, p.getSupervisorID());
            ps.setString(5, p.getCoordinatorID());
            ps.setString(6, p.getProjectID());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ==================== FEEDBACK CRUD ====================

    public static Map<String, Feedback> getFeedbacks() {
        Map<String, Feedback> map = new LinkedHashMap<>();
        try (Statement s = conn().createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM feedbacks")) {
            while (rs.next()) { Feedback f = mapFeedback(rs); map.put(f.getFeedbackID(), f); }
        } catch (SQLException e) { e.printStackTrace(); }
        return map;
    }

    public static List<Feedback> getFeedbacksByProposalID(String proposalID) {
        List<Feedback> list = new ArrayList<>();
        String sql = "SELECT * FROM feedbacks WHERE proposal_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, proposalID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapFeedback(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static void saveFeedback(Feedback f) {
        String sql = "INSERT INTO feedbacks VALUES(?,?,?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, f.getFeedbackID());
            ps.setString(2, f.getContent());
            ps.setFloat(3, f.getScore());
            ps.setString(4, f.getGivenAt().toString());
            ps.setString(5, f.getSupervisorID());
            ps.setString(6, f.getProposalID());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ==================== MILESTONE CRUD ====================

    public static Map<String, Milestone> getMilestones() {
        Map<String, Milestone> map = new LinkedHashMap<>();
        try (Statement s = conn().createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM milestones")) {
            while (rs.next()) { Milestone m = mapMilestone(rs); map.put(m.getMilestoneID(), m); }
        } catch (SQLException e) { e.printStackTrace(); }
        return map;
    }

    public static Milestone getMilestoneByID(String id) {
        String sql = "SELECT * FROM milestones WHERE milestone_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapMilestone(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public static List<Milestone> getMilestonesByProjectID(String projectID) {
        List<Milestone> list = new ArrayList<>();
        String sql = "SELECT * FROM milestones WHERE project_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, projectID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapMilestone(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static void saveMilestone(Milestone m) {
        String sql = "INSERT INTO milestones VALUES(?,?,?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, m.getMilestoneID());
            ps.setString(2, m.getTitle());
            ps.setString(3, m.getDeadline().toString());
            ps.setInt(4, m.isCompleted() ? 1 : 0);
            ps.setFloat(5, m.getCompletionPct());
            ps.setString(6, m.getProjectID());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void updateMilestone(Milestone m) {
        String sql = "UPDATE milestones SET title=?, deadline=?, is_completed=?, completion_pct=? WHERE milestone_id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, m.getTitle());
            ps.setString(2, m.getDeadline().toString());
            ps.setInt(3, m.isCompleted() ? 1 : 0);
            ps.setFloat(4, m.getCompletionPct());
            ps.setString(5, m.getMilestoneID());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ==================== EVAL REPORT CRUD ====================

    public static Map<String, EvalReport> getEvalReports() {
        Map<String, EvalReport> map = new LinkedHashMap<>();
        try (Statement s = conn().createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM eval_reports")) {
            while (rs.next()) { EvalReport r = mapEvalReport(rs); map.put(r.getReportID(), r); }
        } catch (SQLException e) { e.printStackTrace(); }
        return map;
    }

    public static List<EvalReport> getEvalReportsByProjectID(String projectID) {
        List<EvalReport> list = new ArrayList<>();
        String sql = "SELECT * FROM eval_reports WHERE project_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, projectID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapEvalReport(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static void saveEvalReport(EvalReport r) {
        String sql = "INSERT INTO eval_reports VALUES(?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, r.getReportID());
            ps.setInt(2, r.getMarks());
            ps.setString(3, r.getComments());
            ps.setFloat(4, r.getWeightedScore());
            ps.setString(5, r.getSubmittedAt().toString());
            ps.setString(6, r.getEvaluatorID());
            ps.setString(7, r.getProjectID());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ==================== MAPPERS ====================

    private static Connection conn() {
        return DatabaseManager.getConnection();
    }

    private static User mapUser(ResultSet rs) throws SQLException {
        String role = rs.getString("role");
        String userId = rs.getString("user_id");
        String email = rs.getString("email");
        String password = rs.getString("password");
        String fullName = rs.getString("full_name");

        User user;
        switch (Role.valueOf(role)) {
            case STUDENT:
                Student s = new Student(email, password, fullName,
                        rs.getString("student_id"),
                        rs.getString("department"),
                        rs.getFloat("cgpa"));
                String ps = rs.getString("project_status");
                if (ps != null) s.setProjectStatus(ps);
                user = s;
                break;
            case SUPERVISOR:
                user = new Supervisor(email, password, fullName,
                        rs.getString("faculty_id"),
                        rs.getString("department"),
                        rs.getString("specialization"));
                break;
            case COORDINATOR:
                user = new Coordinator(email, password, fullName,
                        rs.getString("coordinator_id"),
                        rs.getString("department"));
                break;
            case EVALUATOR:
                user = new Evaluator(email, password, fullName,
                        rs.getString("evaluator_id"),
                        rs.getString("organization"));
                break;
            case ADMIN:
                user = new Admin(email, password, fullName, "ADMIN");
                break;
            default:
                return null;
        }
        user.setUserID(userId);
        return user;
    }

    private static Proposal mapProposal(ResultSet rs) throws SQLException {
        Proposal p = new Proposal(
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("student_id"));
        p.setProposalID(rs.getString("proposal_id"));
        p.setStatus(ProposalStatus.valueOf(rs.getString("status")));
        String at = rs.getString("submitted_at");
        if (at != null) p.setSubmittedAt(LocalDateTime.parse(at));
        p.setPlagiarismScore(rs.getFloat("plagiarism_score"));
        p.setSupervisorID(rs.getString("supervisor_id"));
        return p;
    }

    private static Project mapProject(ResultSet rs) throws SQLException {
        Project p = new Project(
                rs.getString("title"),
                rs.getString("abstract_text"),
                rs.getString("proposal_id"),
                rs.getString("supervisor_id"),
                rs.getString("coordinator_id"));
        p.setProjectID(rs.getString("project_id"));
        p.setStatus(ProjectStatus.valueOf(rs.getString("status")));
        String sd = rs.getString("submission_date");
        if (sd != null) p.setSubmissionDate(LocalDateTime.parse(sd));
        return p;
    }

    private static Feedback mapFeedback(ResultSet rs) throws SQLException {
        Feedback f = new Feedback(
                rs.getString("content"),
                rs.getFloat("score"),
                rs.getString("supervisor_id"),
                rs.getString("proposal_id"));
        f.setFeedbackID(rs.getString("feedback_id"));
        String ga = rs.getString("given_at");
        if (ga != null) f.setGivenAt(LocalDateTime.parse(ga));
        return f;
    }

    private static Milestone mapMilestone(ResultSet rs) throws SQLException {
        String dl = rs.getString("deadline");
        Milestone m = new Milestone(
                rs.getString("title"),
                dl != null ? LocalDateTime.parse(dl) : LocalDateTime.now(),
                rs.getString("project_id"));
        m.setMilestoneID(rs.getString("milestone_id"));
        m.setCompleted(rs.getInt("is_completed") == 1);
        m.setCompletionPct(rs.getFloat("completion_pct"));
        return m;
    }

    private static EvalReport mapEvalReport(ResultSet rs) throws SQLException {
        EvalReport r = new EvalReport(
                rs.getInt("marks"),
                rs.getString("comments"),
                rs.getString("evaluator_id"),
                rs.getString("project_id"));
        r.setReportID(rs.getString("report_id"));
        r.setWeightedScore(rs.getFloat("weighted_score"));
        String sa = rs.getString("submitted_at");
        if (sa != null) r.setSubmittedAt(LocalDateTime.parse(sa));
        return r;
    }
}
