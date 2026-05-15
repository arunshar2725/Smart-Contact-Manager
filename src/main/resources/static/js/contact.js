const baseURl = "http://localhost:8081";

console.log("This is contact Modal page");

setTimeout(() => {
  const messageBox = document.getElementById("message-box");

  if (messageBox && messageBox.innerHTML.trim() !== "") {
    messageBox.style.transition = "opacity 0.5s ease";
    messageBox.style.opacity = "0";

    setTimeout(() => {
      messageBox.remove();
    }, 500);
  }
}, 3000);

const viewContactModal = document.getElementById("view_contact_modal");

// options with default values
const options = {
  placement: "bottom-right",
  backdrop: "dynamic",
  backdropClasses: "bg-gray-900/50 dark:bg-gray-900/80 fixed inset-0 z-40",
  closable: true,
  onHide: () => {
    console.log("modal is hidden");

    document.activeElement?.blur();
  },
  onShow: () => {
    console.log("modal is shown");
  },
  onToggle: () => {
    console.log("modal has been toggled");
  },
};

// instance options object
const instanceOptions = {
  id: "view_contact_modal",
  override: true,
};

const contactModal = new Modal(viewContactModal, options, instanceOptions);

function openContactModal() {
  contactModal.show();
}

function closeContactModal() {
  contactModal.hide();
}

async function loadContactData(id) {
  console.log("Fetching data for ID: ", id);
  try {
    const response = await fetch(`${baseURl}/api/contacts/${id}`);
    const data = await response.json();
    console.log(data);

    // 1. Text Data Injection
    document.querySelector("#contact_name").textContent = data.name;
    document.querySelector("#contact_email").textContent = data.email;
    document.querySelector("#contact_phone").textContent = data.phoneNumber;

    // Check if address exists, otherwise show 'N/A'
    document.querySelector("#contact_address").textContent = data.address
      ? data.address
      : "Address not provided";

    // Check if description exists
    document.querySelector("#contact_about").textContent = data.description
      ? data.description
      : "No description available.";

    // 2. Handle Profile Picture
    const pictureEl = document.querySelector("#contact_picture");
    if (data.picture) {
      pictureEl.src = data.picture;
    } else {
      // Fallback image if null
      pictureEl.src =
        "https://imgs.search.brave.com/jHDp_R14w-tbRDiYsyiOCGDeCSPE4WqsVfFwiXVDyow/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly90NC5m/dGNkbi5uZXQvanBn/LzExLzY4LzUwLzU3/LzM2MF9GXzExNjg1/MDU3OTRfSUJDRWlh/ZnNJckhGSjA5ZTY1/UDJ2aDUxMTVDMVhJ/N2UuanBn";
    }

    // 3. Handle Favourite Star
    const favEl = document.querySelector("#contact_favourite");
    if (data.favourite) {
      favEl.innerHTML =
        '<span class="bg-yellow-100 text-yellow-800 text-xs font-medium me-2 px-2.5 py-0.5 rounded-full dark:bg-yellow-900 dark:text-yellow-300"><i class="fa-solid fa-star"></i> Favourite</span>';
    } else {
      favEl.innerHTML = ""; // Clear it if not favourite
    }

    // 4. Handle Links (Website & LinkedIn)
    const websiteEl = document.querySelector("#contact_website");
    const linkedInEl = document.querySelector("#contact_linkedin");

    // Reset visibility first
    websiteEl.classList.add("hidden");
    linkedInEl.classList.add("hidden");

    if (data.websiteLink) {
      websiteEl.href = data.websiteLink;
      websiteEl.classList.remove("hidden");
    }

    if (data.linkedInLink) {
      linkedInEl.href = data.linkedInLink;
      linkedInEl.classList.remove("hidden");
    }

    // Finally, show the modal
    openContactModal();
  } catch (error) {
    console.log("Error fetching contact data: ", error);
  }
}

//delete contact
function deleteContact(id, page, size) {
  Swal.fire({
    title: "Are you absolutely sure?",
    text: "This contact will be permanently deleted!",
    icon: "warning",
    showCancelButton: true,
    confirmButtonColor: "#EF4444", // Tailwind red-500
    cancelButtonColor: "#6B7280", // Tailwind gray-500
    confirmButtonText: "Yes, delete it!",
    cancelButtonText: "Cancel",
    background: "#ffffff",
    customClass: {
      // Optional: You can inject Tailwind classes into the SweetAlert elements here if you want extra styling!
      popup: "rounded-2xl shadow-xl",
    },
  }).then((result) => {
    if (result.isConfirmed) {
      // Check if page and size are defined, otherwise default them
      const targetPage = page !== undefined ? page : 0;
      const targetSize = size !== undefined ? size : 10;

      const cleanId = id.trim();
      // Build the redirect URL WITH the query parameters
      const redirectUrl = `/user/contacts/delete/${id}?page=${targetPage}&size=${targetSize}`;

      // Log the URL to the console so you can verify it before the redirect happens!
      console.log("Redirecting to:", redirectUrl);

      window.location.href = redirectUrl;
    }
  });
}

function showAlert(message, icon = "⚠️") {
  document.getElementById("alert-message").textContent = message;
  document.getElementById("alert-icon").textContent = icon;
  document.getElementById("custom-alert").classList.remove("hidden");
}

function closeAlert() {
  document.getElementById("custom-alert").classList.add("hidden");
}

window.onload = function () {
  const selectField = document.getElementById("countries");
  const searchInput = document.getElementById("table-search-users");
  const searchBtn = document.getElementById("search-btn");
  const searchForm = document.querySelector("form");
  searchInput.addEventListener("keydown", function (e) {
    if (e.key === "Enter") {
      e.preventDefault(); // form auto submit band karo
      searchBtn.click(); // same button click logic chalega
    }
  });

  function updateButtonStyle() {
    if (selectField.value !== "" && searchInput.value.trim() !== "") {
      searchBtn.classList.remove("bg-gray-400", "cursor-not-allowed");

      searchBtn.classList.add(
        "bg-blue-600",
        "hover:bg-blue-700",
        "cursor-pointer",
      );
    } else {
      searchBtn.classList.add("bg-gray-400", "cursor-not-allowed");

      searchBtn.classList.remove(
        "bg-blue-600",
        "hover:bg-blue-700",
        "cursor-pointer",
      );
    }
  }

  searchBtn.addEventListener("click", function () {
    if (selectField.value === "" && searchInput.value.trim() === "") {
      showAlert("Please select a field and enter a search value!", "🔍");
      return;
    }
    if (selectField.value === "") {
      showAlert("Please select a field first!", "📋");
      selectField.focus();
      return;
    }
    if (searchInput.value.trim() === "") {
      showAlert("Please enter a search value!", "✏️");
      searchInput.focus();
      return;
    }
    searchForm.submit();
  });

  selectField.addEventListener("change", updateButtonStyle);
  searchInput.addEventListener("input", updateButtonStyle);
};
