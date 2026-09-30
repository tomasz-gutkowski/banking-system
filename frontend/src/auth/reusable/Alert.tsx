type props = {
    messages: string[];
    type: "error" | "success";
}

const styles = {
    shared:`border-l-5 
            rounded-l 
            px-4
            py-3`,
}
function Alert({messages, type}: props) {

    const typeStyle = {
        error:`border-red-400 
               bg-red-100 
               text-red-700`,
        success:`border-green-400
                 bg-green-100 
                 text-green-700`

    }[type];

    const displayMessages = messages.map(
        (msg, i) => <div key={i}>{msg}</div>)
    return (
     <div role="alert">
         <div className={styles.shared+" "+typeStyle}>
             {displayMessages}
         </div>
     </div>
    )
}

export default Alert