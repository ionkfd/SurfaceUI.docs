const searchInput = document.querySelector("#doc-search");
const sections = [...document.querySelectorAll(".doc-section")];
const sidebarLinks = [...document.querySelectorAll(".sidebar a")];
const topButton = document.querySelector(".to-top");

function updateActiveLink() {
  const visibleSections = sections.filter((section) => !section.classList.contains("hidden"));
  const current = visibleSections.findLast((section) => section.getBoundingClientRect().top <= 120) || visibleSections[0];
  if (!current) return;
  sidebarLinks.forEach((link) => {
    link.classList.toggle("active", link.getAttribute("href") === `#${current.id}`);
  });
}

function filterDocs(query) {
  const normalized = query.trim().toLowerCase();
  sections.forEach((section) => {
    const haystack = `${section.textContent} ${section.dataset.search || ""}`.toLowerCase();
    section.classList.toggle("hidden", normalized.length > 0 && !haystack.includes(normalized));
  });
  updateActiveLink();
}

searchInput.addEventListener("input", (event) => filterDocs(event.target.value));

document.querySelectorAll("[data-tabs]").forEach((tabs) => {
  const buttons = [...tabs.querySelectorAll("[data-tab]")];
  const panels = [...tabs.querySelectorAll("[data-panel]")];
  buttons.forEach((button) => {
    button.addEventListener("click", () => {
      buttons.forEach((candidate) => candidate.classList.toggle("active", candidate === button));
      panels.forEach((panel) => panel.classList.toggle("active", panel.dataset.panel === button.dataset.tab));
    });
  });
});

topButton.addEventListener("click", () => {
  window.scrollTo({ top: 0, behavior: "smooth" });
});

window.addEventListener("scroll", () => {
  topButton.classList.toggle("visible", window.scrollY > 600);
  updateActiveLink();
}, { passive: true });

window.addEventListener("hashchange", updateActiveLink);
updateActiveLink();
