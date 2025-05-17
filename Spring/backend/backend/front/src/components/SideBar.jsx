import React from 'react';
import { Link } from 'react-router-dom';
import { Nav } from 'react-bootstrap';
import {
  faGlobe,
  faPalette,
  faImage,
  faLandmark,
  faUsers,
  faHome,
  faKey,
  faUserLock
} from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";

const SideBar = props => {
    const commonLinks = [
        { to: "/countries", icon: faGlobe, text: "Страны" },
        { to: "/artists", icon: faPalette, text: "Художники" },
        { to: "/paintings", icon: faImage, text: "Картины" },
        { to: "/museums", icon: faLandmark, text: "Музеи" },
        { to: "/users", icon: faUsers, text: "Пользователи" },
        { to: "/account", icon: faUserLock, text: "Смена пароля" }
    ];

    return (
        <>
            {props.expanded && (
                <Nav className={"flex-column my-sidebar my-sidebar-expanded"}>
                    {commonLinks.map((link, index) => (
                        <Nav.Item key={index}>
                            <Nav.Link as={Link} to={link.to}>
                                <FontAwesomeIcon icon={link.icon} />{' '}
                                {link.text}
                            </Nav.Link>
                        </Nav.Item>
                    ))}
                </Nav>
            )}

            {!props.expanded && (
                <Nav className={"flex-column my-sidebar my-sidebar-collapsed"}>
                    {commonLinks.map((link, index) => (
                        <Nav.Item key={index}>
                            <Nav.Link as={Link} to={link.to}>
                                <FontAwesomeIcon icon={link.icon} size="lg" />
                            </Nav.Link>
                        </Nav.Item>
                    ))}
                </Nav>
            )}
        </>
    );
};

export default SideBar;