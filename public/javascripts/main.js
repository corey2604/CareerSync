import React from 'react';
import ReactDOM from 'react-dom';

var App = React.createClass({
    render: function() {
        return(
            <h1>Hello</h1>
        )
    }
});

ReactDOM.render(<App />, document.getElementById('root'));