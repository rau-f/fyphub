package repository;

import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:fyphub.db";
    private static Connection connection;

    public static void initialize() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            connection.setAutoCommit(true);
            createSchema();
            seedIfEmpty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void close() {
        try {
            if (connection != null && !connection.isClosed())
                connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createSchema() throws SQLException {
        Statement s = connection.createStatement();

        s.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    user_id TEXT PRIMARY KEY,
                    email TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    role TEXT NOT NULL,
                    full_name TEXT NOT NULL,
                    student_id TEXT,
                    department TEXT,
                    cgpa REAL,
                    project_status TEXT,
                    faculty_id TEXT,
                    specialization TEXT,
                    coordinator_id TEXT,
                    evaluator_id TEXT,
                    organization TEXT
                )""");

        s.execute("""
                CREATE TABLE IF NOT EXISTS proposals (
                    proposal_id TEXT PRIMARY KEY,
                    title TEXT NOT NULL,
                    description TEXT,
                    status TEXT NOT NULL,
                    submitted_at TEXT,
                    plagiarism_score REAL DEFAULT 0.0,
                    student_id TEXT REFERENCES users(user_id),
                    supervisor_id TEXT REFERENCES users(user_id)
                )""");

        s.execute("""
                CREATE TABLE IF NOT EXISTS projects (
                    project_id TEXT PRIMARY KEY,
                    title TEXT NOT NULL,
                    abstract_text TEXT,
                    status TEXT NOT NULL,
                    submission_date TEXT,
                    proposal_id TEXT REFERENCES proposals(proposal_id),
                    supervisor_id TEXT REFERENCES users(user_id),
                    coordinator_id TEXT REFERENCES users(user_id)
                )""");

        s.execute("""
                CREATE TABLE IF NOT EXISTS feedbacks (
                    feedback_id TEXT PRIMARY KEY,
                    content TEXT,
                    score REAL,
                    given_at TEXT,
                    supervisor_id TEXT REFERENCES users(user_id),
                    proposal_id TEXT REFERENCES proposals(proposal_id)
                )""");

        s.execute("""
                CREATE TABLE IF NOT EXISTS milestones (
                    milestone_id TEXT PRIMARY KEY,
                    title TEXT NOT NULL,
                    deadline TEXT,
                    is_completed INTEGER DEFAULT 0,
                    completion_pct REAL DEFAULT 0.0,
                    project_id TEXT REFERENCES projects(project_id)
                )""");

        s.execute("""
                CREATE TABLE IF NOT EXISTS eval_reports (
                    report_id TEXT PRIMARY KEY,
                    marks INTEGER,
                    comments TEXT,
                    weighted_score REAL DEFAULT 0.0,
                    submitted_at TEXT,
                    evaluator_id TEXT REFERENCES users(user_id),
                    project_id TEXT REFERENCES projects(project_id)
                )""");

        s.close();
    }

    private static void seedIfEmpty() throws SQLException {
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM users");
        rs.next();
        int count = rs.getInt(1);
        rs.close();
        st.close();

        if (count > 0)
            return;

        String[] seeds = {
                // Students
                "INSERT INTO users VALUES('u-stu1','student1@nu.edu.pk','1234','STUDENT','Ali Khan','STU-001','Computer Science',3.5,'NONE',NULL,NULL,NULL,NULL,NULL)",
                "INSERT INTO users VALUES('u-stu2','student2@nu.edu.pk','1234','STUDENT','Sara Ahmed','STU-002','Computer Science',3.8,'NONE',NULL,NULL,NULL,NULL,NULL)",
                // Coordinator
                "INSERT INTO users VALUES('u-coord1','coord1@nu.edu.pk','1234','COORDINATOR','Dr. Haroon',NULL,'Computer Science',NULL,NULL,NULL,NULL,'COORD-001',NULL,NULL)",
                // Supervisors
                "INSERT INTO users VALUES('u-sup1','sup1@nu.edu.pk','1234','SUPERVISOR','Dr. Malik',NULL,'Computer Science',NULL,NULL,'FAC-001','Machine Learning',NULL,NULL,NULL)",
                "INSERT INTO users VALUES('u-sup2','sup2@nu.edu.pk','1234','SUPERVISOR','Dr. Zara',NULL,'Computer Science',NULL,NULL,'FAC-002','Software Engineering',NULL,NULL,NULL)",
                // Evaluator
                "INSERT INTO users VALUES('u-eval1','eval1@nu.edu.pk','1234','EVALUATOR','Engr. Bilal',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'EVAL-001','Tech Corp')",
                // Admin
                "INSERT INTO users VALUES('u-admin1','admin@nu.edu.pk','admin123','ADMIN','System Admin',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL)",
                // Baseline proposals (APPROVED, for plagiarism)
                "INSERT INTO proposals VALUES('p-prop1','Smart Attendance System','A face recognition based attendance system using deep learning neural network for automated student attendance tracking in classrooms','APPROVED','2026-05-01T10:00:00',0.0,'u-stu1',NULL)",
                "INSERT INTO proposals VALUES('p-prop2','Hospital Management System','A comprehensive patient records appointment scheduling database system for managing hospital operations and medical data efficiently','APPROVED','2026-05-01T10:00:00',0.0,'u-stu2',NULL)"
        };

        Statement batch = connection.createStatement();
        for (String sql : seeds)
            batch.execute(sql);
        batch.close();
    }

    public static void resetDatabase() {
        try {
            Statement s = connection.createStatement();
            s.execute("DROP TABLE IF EXISTS eval_reports");
            s.execute("DROP TABLE IF EXISTS milestones");
            s.execute("DROP TABLE IF EXISTS feedbacks");
            s.execute("DROP TABLE IF EXISTS projects");
            s.execute("DROP TABLE IF EXISTS proposals");
            s.execute("DROP TABLE IF EXISTS users");
            s.close();
            createSchema();
            seedIfEmpty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to reset database", e);
        }
    }
}
