/* ---------------------------------------------------------
   학생 등록 · 수정 페이지 — 주소 "/new" 와 "/edit/:id"
   등록인지 수정인지는 주소에 id 가 붙어 있는지로 가릅니다.

   6부와 달라진 곳은 둘입니다.
     (1) 서버에 보내는 일을 store 의 saveStudent 가 맡는다
     (2) 메시지를 navigate 에 실어 보내지 않는다.
         store 에 넣으면 목록 페이지에서 저절로 보인다.

   form 은 그대로 이 페이지의 useState 에 둡니다. 입력 중인 값은
   이 화면에서만 쓰고 버리는 것이라 store 에 둘 이유가 없습니다.
   --------------------------------------------------------- */

import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";

import { fetchStudent } from "../api/studentApi.js";
import { validateStudent } from "../lib/validation.js";
import { EMPTY_FORM, toRequest, toFormValues } from "../lib/studentData.js";
import { useStudentStore } from "../store/studentStore.js";
import StudentForm from "../components/StudentForm.jsx";

function StudentFormPage() {
    const { id } = useParams();
    const isEditing = id !== undefined;
    const navigate = useNavigate();

    // 입력 중인 값은 이 화면만의 것이다.
    const [form, setForm] = useState(EMPTY_FORM);
    const [loading, setLoading] = useState(false);

    // 서버에 보내는 일과 메시지는 store 가 맡는다.
    const saveStudent = useStudentStore((s) => s.saveStudent);
    const showError = useStudentStore((s) => s.showError);
    const clearMessage = useStudentStore((s) => s.clearMessage);
    
    //const loading = useStudentStore((s) => s.loading);

    // 수정 모드면 서버에서 그 학생을 불러와 폼을 채운다.
    useEffect(() => {
        if (!isEditing) {
            return;
        }

        let cancelled = false;

        async function loadStudent() {
            setLoading(true);
            try {
                const student = await fetchStudent(id);
                // 불러오는 도중에 다른 페이지로 떠났으면 state 를 건드리지 않는다.
                if (!cancelled) {
                    setForm(toFormValues(student));
                }
            } catch (error) {
                console.error("Error:", error);
                if (!cancelled) {
                    showError(error.message);
                }
            } finally {
                if (!cancelled) {
                    setLoading(false);
                }
            }
        }

        loadStudent();

        // 정리 함수 — 이 페이지를 떠날 때 위 플래그를 올린다.
        return () => {
            cancelled = true;
        };
    }, [id, isEditing, showError]);

    function handleChange(event) {
        // 입력칸 여섯 개가 모두 이 함수 하나를 부른다.
        // 어느 칸인지는 input 에 적어 둔 name 이 알려 준다.
        const name = event.target.name;
        const value = event.target.value;

        const next = { ...form };
        next[name] = value;
        setForm(next);
    }

    async function handleSubmit(event) {
        event.preventDefault();
        clearMessage();

        const studentData = toRequest(form);

        const errorMessage = validateStudent(studentData);
        if (errorMessage) {
            showError(errorMessage);
            return;
        }

        /* 성공하면 true 가 돌아온다. 성공 문구는 store 가 이미 넣어 두었으므로
           여기서는 옮겨 가기만 하면 된다. navigate 에 값을 실을 필요가 없다. */
        const saved = await saveStudent(isEditing ? id : null, studentData);

        if (saved) {
            navigate("/");
        }
    }

    function handleCancel() {
        clearMessage();
        navigate("/");
    }

    return (
        <div className="page">
            <Link to="/" className="back-link">&larr; 학생 목록으로</Link>

            {loading && <div className="loading">불러오는 중...</div>}

            <StudentForm
                form={form}
                isEditing={isEditing}
                onChange={handleChange}
                onSubmit={handleSubmit}
                onCancel={handleCancel}
            />
        </div>
    );
}

export default StudentFormPage;